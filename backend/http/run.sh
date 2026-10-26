#!/bin/sh
# Запускает HTTP-сценарии Hurl против развёрнутого сервера.
#
# Перед каждым файлом сценария очищает тестовую БД скриптом db/clean-test-database.sql;
# скрипт отказывается работать с базой, имя которой не оканчивается на _test.
# Сервер должен работать с той же тестовой БД.
#
# Переменные:
#   IS_LAB1_BASE_URL  адрес API, по умолчанию http://localhost:8080/api/v1
#   PSQL              команда psql для тестовой БД, по умолчанию psql; параметры
#                     подключения берутся из PGHOST, PGPORT, PGUSER, PGPASSWORD, PGDATABASE
#
# Примеры:
#   PGDATABASE=is_lab1_test PGUSER=is_lab1 sh http/run.sh
#   sh http/run.sh http/auth/register_login_session_logout.hurl
#
# Покрытие кода сценариями Hurl (порог 85% в ./gradlew check считается вместе с JUnit):
#   ./gradlew war preparePostgresDriver prepareJacocoAgent
#   JAVA_OPTS="-javaagent:$PWD/build/server/jacocoagent.jar=destfile=$PWD/build/jacoco/hurl.exec,includes=com.github.vodobryshkin.*" \
#           $WILDFLY_HOME/bin/standalone.sh
#   sh http/run.sh
#   $WILDFLY_HOME/bin/jboss-cli.sh -c --command=:shutdown   # агент пишет hurl.exec при остановке
#   ./gradlew check jacocoTestReport
# Между запуском сервера и check классы не пересобираются, иначе покрытие не сопоставится.
set -eu

script_dir=$(cd "$(dirname "$0")" && pwd)
clean_script="$script_dir/../db/clean-test-database.sql"
base_url=${IS_LAB1_BASE_URL:-http://localhost:8080/api/v1}
psql_command=${PSQL:-psql}

if [ "$#" -eq 0 ]; then
    set -- $(find "$script_dir" -name '*.hurl' | sort)
fi

failed=0
for scenario in "$@"; do
    $psql_command -q -v ON_ERROR_STOP=1 -1 < "$clean_script"
    if ! hurl --test --variable "base_url=$base_url" "$scenario"; then
        failed=1
    fi
done

exit "$failed"

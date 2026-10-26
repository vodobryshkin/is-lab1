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
    fixture="${scenario%.hurl}.sql"
    if [ -f "$fixture" ]; then
        $psql_command -q -v ON_ERROR_STOP=1 -1 < "$fixture"
    fi
    if ! hurl --test --variable "base_url=$base_url" "$scenario"; then
        failed=1
    fi
done

exit "$failed"

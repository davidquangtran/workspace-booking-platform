#!/bin/bash
set -e
set -u

# List extensions to install in every database
EXTENSIONS="pgcrypto uuid-ossp citext pg_trgm unaccent vector"

# Function to install extensions in a database
function install_extensions_in_db() {
    local database=$1
    echo "Installing extensions in database '$database'"
    for ext in $EXTENSIONS; do
        echo "  - Creating extension: $ext"
        psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$database" <<-EOSQL
            CREATE EXTENSION IF NOT EXISTS "$ext";
EOSQL
    done
}

# Install in default postgres DB
install_extensions_in_db "postgres"

# Install in all databases listed in POSTGRES_MULTIPLE_DATABASES
if [ -n "${POSTGRES_MULTIPLE_DATABASES:-}" ]; then
    for db in $(echo $POSTGRES_MULTIPLE_DATABASES | tr ',' ' '); do
        install_extensions_in_db "$db"
    done
fi

echo "All extensions installed successfully"
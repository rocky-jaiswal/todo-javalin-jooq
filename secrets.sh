#!/usr/bin/env bash

# Default values
COMMAND="encrypt"
ENVIRONMENT="local"

# Parse arguments
while [[ $# -gt 0 ]]; do
  case $1 in
    -c|--command)
      COMMAND="$2"
      shift 2
      ;;
    -e|--environment)
      ENVIRONMENT="$2"
      shift 2
      ;;
    *)
      echo "Unknown option: $1"
      echo "Usage: $0 -c <command> -e <environment>"
      exit 1
      ;;
  esac
done

# Validate
if [[ -z "$COMMAND" || -z "$ENVIRONMENT" ]]; then
  echo "Error: both --command and --environment must be provided"
  exit 1
fi

# Example logic: run different commands based on input
if [[ "$COMMAND" == "encrypt" ]]; then
  echo "Running encrypt for environment: $ENVIRONMENT"
  # Example command
  npx dotenvx encrypt -f app/src/main/resources/.env."$ENVIRONMENT" --stdout > app/src/main/resources/.env."$ENVIRONMENT".enc
elif [[ "$COMMAND" == "decrypt" ]]; then
  echo "Running decrypt for environment: $ENVIRONMENT"
  # Example command
  npx dotenvx decrypt -f app/src/main/resources/.env."$ENVIRONMENT".enc --stdout > app/src/main/resources/.env."$ENVIRONMENT"
else
  echo "Unknown command: $COMMAND"
  exit 1
fi
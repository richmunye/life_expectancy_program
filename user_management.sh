#!/bin/bash

# User data file
USER_DATA_FILE="users.csv"

# Create the CSV file if it doesn't exist
if [ ! -f "$USER_DATA_FILE" ]; then
  echo "email,uuid,first_name,last_name,password,dob,hiv_status,diagnosis_date,art_status,art_start_date,country_iso" >"$USER_DATA_FILE"
fi

# Function to get user by email
get_by_email() {
  local email=$1
  if grep -q "^$email," "$USER_DATA_FILE"; then
    user_data=$(grep "^$email," "$USER_DATA_FILE")
    uuid=$(echo "$user_data" | cut -d, -f2)
    first_name=$(echo "$user_data" | cut -d, -f3)
    echo "$uuid,$first_name"
  fi
}

verify_uuid() {
  local uuid=$1
  echo "$uuid uuid provided values"
  if grep -q "^$uuid," "$USER_DATA_FILE"; then
    user_data=$(grep "^$uuid," "$USER_DATA_FILE")
    email=$(echo "$user_data" | cut -d, -f1)
    echo "$uuid, $email"
  fi
}

# Function to register user
register_user() {
  local email=$1

  if grep -q "^$email," "$USER_DATA_FILE"; then
    echo "exists"
  else
    local uuid=$(uuidgen)
    echo "$email,$uuid,,,,,,,,,," >>"$USER_DATA_FILE"
    echo "$uuid"
  fi
}

# Function to update user profile
update_profile() {
  local uuid="$1"
  local email="$2"
  local first_name="$3"
  local last_name="$4"
  local password="$5"
  local dob="$6"
  local hiv_status="$7"
  local diagnosis_date="$8"
  local art_status="$9"
  local art_start_date="${10}"
  local country_iso="${11}"

  # Use openssl to hash the password
  hashed_password=$(echo -n "$password" | openssl dgst -sha256 | awk '{print $2}')

  # Remove the old entry
  sed -i "/^$email,/d" "$USER_DATA_FILE"

  # Add the updated entry
  echo "$email,$uuid,$first_name,$last_name,$hashed_password,$dob,$hiv_status,$diagnosis_date,$art_status,$art_start_date,$country_iso" >>"$USER_DATA_FILE"
}

# Function to validate password
validate_password() {
  local email=$1
  local password=$2
  hashed_password=$(echo -n "$password" | openssl dgst -sha256 | awk '{print $2}')
  stored_password=$(grep "^$email," "$USER_DATA_FILE" | cut -d, -f5)

  if [ "$hashed_password" = "$stored_password" ]; then
    echo "valid"
  else
    echo "invalid"
  fi
}

# Main script logic
command=$1
shift

case $command in
"get_by_email")
  get_by_email "$1"
  ;;
"register")
  register_user "$1"
  ;;
"verify_uuid")
  verify_uuid "$1"
  ;;
"update_profile")
  update_profile "$@"
  ;;
"validate_password")
  validate_password "$1" "$2"
  ;;
*)
  echo "Invalid command"
  ;;
esac

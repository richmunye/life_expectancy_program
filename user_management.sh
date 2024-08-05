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
  if grep -q "$uuid," "$USER_DATA_FILE"; then
    user_data=$(grep "$uuid," "$USER_DATA_FILE")
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
  hashed_password=$(echo "$password" | openssl passwd -salt vZJw1PdD --stdin)
  echo "$email" "$USER_DATA_FILE"
  # Check if user exists and needs to complete registration
  if grep -q "$email" "$USER_DATA_FILE"; then
    # Use awk to update the user's information in the CSV file
    awk -F, -v OFS=, -v email="$email" \
      -v first_name="$first_name" \
      -v last_name="$last_name" \
      -v password="$hashed_password" \
      -v dob="$dob" \
      -v diagnosis_status="$hiv_status" \
      -v diagnosis_date="$diagnosis_date" \
      -v art_status="$art_status" \
      -v art_start_date="$art_start_date" \
      -v country_iso="$country_iso" \
      '$1 == email { $3=first_name; $4=last_name; $5=password; $6=dob; $7=diagnosis_status; $8=diagnosis_date; $9=art_status; $10=art_start_date; $11=country_iso } 1' "$USER_DATA_FILE" >temp.csv && mv temp.csv "$USER_DATA_FILE"

    echo "User registration completed for $first_name $last_name."
  else
    echo "User with email$email does not exist. Please register first."
  fi
}

# Function to validate password
validate_password() {
  local email=$1
  local password=$2
  # hashed_password=$(echo -n "$password" | openssl dgst -sha256 | awk '{print $2}')
  # stored_password=$(grep "^$email," "$USER_DATA_FILE" | cut -d, -f5)
  if grep -q "$email" $USER_DATA_FILE; then
    userData=$(grep "$email" $USER_DATA_FILE)
    first_name=$(echo "$userData" | cut -d ',' -f 3)
    last_name=$(echo "$userData" | cut -d ',' -f 4)
    hashed_password=$(echo "$password" | openssl passwd -salt vZJw1PdD --stdin)
    stored_password=$(grep "^$email," "$USER_DATA_FILE" | cut -d, -f5)
    if [[ "$hashed_password" == "$stored_password" ]]; then
      echo "valid"
    else
      echo "invalid"
    fi
  else
    echo "User not found"
  fi
}

# Function to View Patient's Data
view_profile() {
  local email=$1
  # hashed_password=$(echo -n "$password" | openssl dgst -sha256 | awk '{print $2}')
  # stored_password=$(grep "^$email," "$USER_DATA_FILE" | cut -d, -f5)
  if grep -q "$email" $USER_DATA_FILE; then
    userData=$(grep "$email" $USER_DATA_FILE)
    echo "$userData"
  else
    echo "User not found"
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
"view_profile")
  view_profile "$1"
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

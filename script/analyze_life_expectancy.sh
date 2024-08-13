#!/bin/bash

# User data file
USER_DATA_FILE="Resources/users.csv"


analyze_life_expectancy() {
    local csv_file="$USER_DATA_FILE"
    local column_number="$1"

    # Extract the life expectancy values
    values=$(cut -d',' -f"$column_number" "$csv_file" | tail -n +2)

    # Calculate mean
    mean=$(echo "$values" | awk '{sum+=$1} END {print sum/NR}')

    # Calculate median
    median=$(echo "$values" | sort -n | awk '{
        count[NR] = $1;
    } END {
        if (NR % 2) {
            print count[(NR + 1) / 2];
        } else {
            print (count[NR/2] + count[(NR/2) + 1]) / 2.0;
        }
    }')

    # Calculate mode
    mode=$(echo "$values" | sort -n | uniq -c | sort -rn | awk 'NR==1 {print $2}')

    timestamp=$(date +"%Y%m%d%H%M%S")
    output_file="analytics/system_analytics_${timestamp}.csv"
    echo "Metric,Value" > "$output_file"
    # shellcheck disable=SC2129
    echo "Mean,$mean" >> "$output_file"
    echo "Median,$median" >> "$output_file"
    echo "Mode,$mode" >> "$output_file"

    echo "Analytics data exported to $output_file"

}

#!/bin/bash

update_user_life_span() {
    local csv_file="$USER_DATA_FILE"
    local email="$1"
    local new_life_span="$2"
    local temp_file="${USER_DATA_FILE}.tmp"

    # Check if the CSV file exists
    if [ ! -f "$csv_file" ]; then
        echo "Error: CSV file not found."
        return 1
    fi
    echo "$email  ===> $new_life_span"
    # Get the column numbers for email and life_span
    header=$(head -n 1 "$csv_file")
    email_col=$(echo "$header" | awk -F',' '{for(i=1;i<=NF;i++) if($i=="email") print i}')
    life_span_col=$(echo "$header" | awk -F',' '{for(i=1;i<=NF;i++) if($i=="life_span") print i}')

    if [ -z "$email_col" ] || [ -z "$life_span_col" ]; then
        echo "Error: Required columns 'email' or 'life_span' not found in CSV."
        return 1
    fi

    # Update the CSV file
    awk -F',' -v email="$email" -v new_life_span="$new_life_span" \
        -v email_col="$email_col" -v life_span_col="$life_span_col" '
    BEGIN {OFS=","}
    {
        if (NR == 1 || $email_col != email) {
            print $0
        } else {
            $life_span_col = new_life_span
            print $0
        }
    }' "$csv_file" > "$temp_file"

    # Check if any changes were made
    if cmp -s "$csv_file" "$temp_file"; then
        echo "No user found with email: $email"
        rm "$temp_file"
        return 1
    else
        mv "$temp_file" "$csv_file"
        return 0
    fi
}

# Usage example:
# update_user_life_span "users.csv" "user@example.com" "85.5"


command=$1
shift

case $command in
"analyze_life_expectancy")
  analyze_life_expectancy "$1"
  ;;
"update_user_life_span")
  update_user_life_span "$1" "$2"
  ;;

esac
# Usage example:
# analyze_life_expectancy "users_life_expectancy.csv" 3
#!/bin/bash

CSV_FILE="Resources/users.csv"
ACTION="$1"
EMAIL="$2"

read_user() {
    while IFS=',' read -r email uuid firstname lastname password dob hiv_status hivData artStatus artDate countryIso life_span; do
        if [[ "$email" == "$EMAIL" ]]; then
            echo "first_name:$firstname,last_name:$lastname,dob:$dob,hiv_status:$hiv_status,diagnosis_date:$hivData,art_status:$artStatus,art_start_date:$artDate,country_iso:$countryIso,life_span:$life_span"
            return
        fi
    done < <(tail -n +2 "$CSV_FILE") # Skip the header
    echo "User not found"
}

update_user() {
    FIELD="$3"
    NEW_VALUE="$4"
    TEMP_FILE=$(mktemp)
    header_line=$(awk 'NR==1' "$CSV_FILE")
    IFS=',' read -r -a HEADERS <<<"$header_line"
    echo "$header_line"

    FIELD_INDEX=-1
    for i in "${!HEADERS[@]}"; do
        echo "${HEADERS[$i]}"
        if [[ "${HEADERS[$i]}" == "$FIELD" ]]; then
            FIELD_INDEX=$i
            break
        fi
    done
    echo "$FIELD_INDEX"
    if [[ $FIELD_INDEX -eq -1 ]]; then
        echo "Field not found"
        exit 1
    fi

    {
        # shellcheck disable=SC2001
        echo "${HEADERS[*]}" | sed 's/ /,/g'
        while IFS=',' read -r -a VALUES; do
            if [[ "${VALUES[0]}" == "$EMAIL" ]]; then
                VALUES[$FIELD_INDEX]="$NEW_VALUE"
            fi
            # shellcheck disable=SC2001
            echo "${VALUES[*]}" | sed 's/ /,/g'
        done
    } < <(tail -n +2 "$CSV_FILE") >"$TEMP_FILE"

    mv "$TEMP_FILE" "$CSV_FILE"
    echo "User updated successfully"
}

case "$ACTION" in
read)
    read_user
    ;;
update)
    update_user "$@"
    ;;
*)
    echo "Unknown action: $ACTION"
    ;;
esac

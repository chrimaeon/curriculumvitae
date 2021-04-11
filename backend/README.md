# Curriculum Vitae BFF

## Available API's #####

GET

### /profile ###

| Name | Type   | In    | Description                                         |
| ---- | ------ | ----- | --------------------------------------------------- |
| lang | String | Query | The language to return the answer; Defaults to 'en' |

###### Response ######

    {
      "name": "My Name",
      "phone": "+1234567890",
      "profileImageUrl": "https://via.placeholder.com/150",
      "address": {
        "street": "Main Street 1",
        "city": "My Hometown",
        "postalCode": "42"
      },
      "email": "noreply@test.com",
      "intro": [
        "Intro line 1 ...",
        "Intro line 2 ...",
        "etc ... etc ... etc ..."
      ],
      "lang": "en"
    }

GET

### /employment ###

| Name | Type   | In    | Description                                         |
| ---- | ------ | ----- | --------------------------------------------------- |
| lang | String | Query | The language to return the answer; Defaults to 'en' |

###### Response ######

    {
      "jobTitle": "Software Developer",
      "employer": "CMG Mobile Apps",
      "startDate": "2010-06-01",
      "endDate": null,
      "city": "Graz",
      "description": [
        "Founder",
        "Software development"
      ]
    }

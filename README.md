FBU Final Project - Trip Scheduler
===

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Keep track of your favorite locations that you want to vacation to, as well as popular stops for every city. Plan your trips by checking the availability of locations for every day and adding them to a saved list.

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** 
- **Mobile:**
    - Access account even offline
    - Easy access no matter where you are 
- **Story:** Users can maintain their trip itineraries by viewing their favorite locations.
- **Market:** Anyone who is planning to take a vacation
- **Habit:** 
    - Probably not addictive, but good for forming organizational habits. 
- **Scope:** 
    - The basis (favorites page, searching for locations, creating itineraries) will be tricky, but will probably take 1-2 weeks
    - Implementing more advanced features (i.e. adding locations to an itinerary, accessing which places are open) it may be harder

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Users can log into their account
* User can search for locations [Google Places API](https://developers.google.com/maps/documentation/places/android-sdk/overview)
* User can view details about each location and favorite them
    * Details: description, opening hours, ratings
* Create new itineraries with location, date, etc
* Add locations to an itinerary with a certain date/time

**Optional Nice-to-have Stories**

* Before adding location to itinerary, check opening hours on the specified date/time (if applicable)
* Display current weather forecast for cities/towns on the details page ([AccuWeather](https://developer.accuweather.com/api-flow-diagram) and the [National Weather Service](https://www.weather.gov/documentation/services-web-api#) have APIs)
* Allow user to add multiple countries/cities per itinerary
* Allow the user to put in personal destinations/to do (i.e. "Sightseeing", "Window shopping", "Drop Dad off at airport")
* Change color/give mark when a location is currently closed
* Pull reviews off of Yelp
* Add price level for destinations


### 2. Screen Archetypes

* Log in Screen
    * User can log into their account
* Registration Screen
    * User can make an account
* Main Activity/Screen
   * List of planned vacations
       * Each itinerary has the name of the trip and the date
   * FAB to add new trips
* Search Screen
    * User can search for locations using Google Places API
    * Clicking on a suggested location brings them to a detailed view about that location
* Detailed Activity/Screen
   * View locations in detail
   * Details include:
       * Name
       * Description
       * Opening hours (for stores/museums/etc)
       * Weather forecast (for cities/countries)
       * Reviews (for a specific location)/popular locations (for cities/countries)
   * Button to favorite the place
   * Button to add to itinerary
   * Button to add a review/popular location 
 * Favorites Activity/Screen
   * List of saved locations
   * View the title and notes you saved with it
   * Clicking leads you to detailed view of the location (see above)
 * Add/Edit new favorite Activity/Screen
   * Promps user for notes about this location
   * Name and location of the favorite place are autofilled, but can be edited
   * Saving the screen saves the favorites to the user's saved list
sing FAB
 * Add/edit add to itinerary Screen
   * Asks user for the trip to add to, date to travel on, time to visit at, and additional notes
   * Saving will add the location to the appropriate part of their itinerary
 * Add/edit new itinerary Screen
   * Asks user for the name, location, dates, and notes for new itineraries
   * Saving will add new itinerary to the user's dashboard

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home dashboard
* Global search
* Favorite Locations
* User page

**Flow Navigation** (Screen to Screen)

* Log in screen
   * Registration Screen
   * Main Screen
* Registration Screen
   * Main Screen
* Main Screen
   * New itinerary Screen
* Global search Screen
   * Detailed city/location Screen
* Favorite Locations
   * Detailed city/location screen
* Detailed city/location Locations
   * Add to itinerary screen
   * Add to favorites screen

## Wireframes
![](https://i.imgur.com/iV1PIEu.jpg)


## Schema 

### Model: Itinerary

|  Property |  Type |  Description |
| -------- | -------- | -------- |
| objectID     | String     | unique id for the itinerary (default)     |
| title | String | user gives title to itinerary |
| description | String | user gives notes about the trip |
| author | Pointer to User | author of the itinerary |
| placeID     | String     | id of location given by Google Places API|
| startDate | DateTime | day the trip begins |
| endDate | DateTime | day the trip ends |
| desintations | JSON Object| JSON of locations the user wants to visit|

### Model: User

| Property | Type | Description |
| -------- | -------- | -------- |
| objectID     | String     | default id for user     |
| username | String | username the user should log in with |
| password | String | user's password |
| email | String | email the user signs up with |
| favorites | Array | Array of pointers to Favorites |
| itineraries | Array | Array of pointers to Itinerary |

### Model: Favorites

| Property | Type | Description |
| -------- | -------- | -------- |
| objectID     | String     | default id for the favorite location |
| placeID | String | place id according to Google Places API
| notes | String | user's notes about the location |

## Networking
### Parse Network Requests
* Home Page
    * (Read/GET) fetch user's itineraries to display 
    * (Delete/DELETE) remove itinerary from user's list
* New/Edit Itinerary Page
    * (Read/GET) fetch itinerary data if it is being edited (data already exists)
    * (Create/POST) create a new itinerary (for adding)
    * (Update/PUT) update existing itinerary (for editing)
    * (Update/PUT) update user's itinerary list
* New/Edit destination Page
    * (Read/GET) fetch location data if it is being edited (data already exists)
    * (Create/POST) create a new destination (for adding)
    * (Update/PUT) update existing destination (for editing)
    * (Update/PUT) update itinerary's destination list
* Location Page
    * (Create/POST) add the current location to user's favorites
    * (Delete/DELETE) remove the current location from user's favorites
* Sign-up Page
    * (Create/POST) add user's email, username, and password

### Network Requests for Existing APIs

[Google Places API - SDK for Android](https://developers.google.com/maps/documentation/places/android-sdk/place-details)

| HTTP Verb | Endpoint | Description |
| -------- | -------- | -------- |
| GET     | Place.Field.ID     | id for a location |
| GET | Place.Field.NAME | name of a location |
| GET | Place.Field.OPENING_HOURS | when a location is open | 
| GET | Place.Field.TYPES | list of types that describe the location |
| GET | Place.Field.RATING | average rating of users on Google |
| GET | Place.Field.PRICE_LEVEL | rating for how expensive the location is |

[Yelp Fusion API](https://www.yelp.com/developers/documentation/v3)
Base Url: https://api.yelp.com/v3/
| HTTP Verb | Endpoint | Description |
| -------- | -------- | -------- |
| GET     | /businesses/{id}/reviews  | top 3 reviews for a business |



FBU Final Project - School Scheduler
===

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Keep track of everything in your academic life in one app; view your class and club schedules, stay on top of your deadlines, and set reminders for your tasks.

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Education
- **Mobile:**
    - Access account even offline
    - Easy access no matter where you are 
    - Push notifications for events/reminders
- **Story:** Users can maintain their schedule and set reminders for events or assignements.
- **Market:** All students (especially college students)
- **Habit:** 
    - Probably not addictive, but good for forming organizational habits. 
    - Users would use it daily
- **Scope:** 
    - The basis (a schedule creater with push notifs) will probably not be too bad (1-2 weeks)
    - Expanding it may be harder (location basis, etc)

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Users can log into their account
* User can insert classes/other events to a calendar
* User gets push notifications before events
* Create assignments and get notifications for them

**Optional Nice-to-have Stories**

* Daily/weekly overview - able to add goals to it as well
* Import events from Google Calendar
* Sync with Student Canvas (not sure if they have an API/SDK)
    * they do! [link](https://canvas.instructure.com/doc/api/assignments.html)

**Optional Very Stretch Stories**

* Give user notifications when they should leave/prepare for an event
    * Like how Google Calendar/Maps gives notifs on when you should leave to arrive somewhere on time

### 2. Screen Archetypes

* Log in Screen
    * User can log into their account
* Registration Screen
    * User can make an account
* Main Activity/Screen
   * List of upcoming events/assignments (like the schedule view in gcal)
   * User can click on each item to view details
   * Long click to edit items
   * FAB to add new items
       * If have time: integrate importing assignments from Canvas
* Detailed Activity/Screen
   * View events/assignments in detail
   * Details include:
       * Name
       * Description
       * Date/time/deadline
       * Class/club/other tags
       * Location (either a place or a Zoom link maybe?)
       * Existing notifications
   * Button to edit 
 * Edit Activity/Screen
   * Change all details listed above ^^
 * Schedule Activity/Screen
   * View your weekly schedule (classes/clubs - no assignments/reminders!)
   * Add recurring events
sing FAB
 * Edit Class/Recurring events Activity/Screen
   * Change all details listed above ^^

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Assignments schedule/stream (Main)
* Weekly schedule

**Flow Navigation** (Screen to Screen)

* Log in screen
   * Registration Screen
   * Main Screen
* Registration Screen
   * Main Screen
* Main Screen
   * Detailed Screen
   * Weekly Schedule Screen
* Detailed Screen
   * Main Screen
   * Edit Screen
* Edit Screen
   * Detailed Screen
* Weekly Schedule Screen
   * Main Screen

## Wireframes
To be improved lol
![](https://i.imgur.com/6EoUW5V.png)

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]

### Models
[Add table of models]

### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]

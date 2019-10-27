# Design Strategy
## Introduction
This kata is about keeping an application architecture open
for extension, but close for change. That means: the core
functionality (selecting persons of the day for greeting, and
create greetings for them), it shall be decoupled from
peripheral details. It should not matter which tools we use
to persist and provide our input
data (the person records we need). It should not be of significance
to the core functionality, how the notifications will be sent to
the persons to be greeted.

The birthday greeter application shall be independent of
its caller, and the core (i.e. the business rules) shall be
self contained in a single component package. Peripheral
functionality shall be driven through interfaces and/or adapters
and shall be provided by different components.
Dependencies shall be injected into the core package
during runtime, and before the core functionality gets run.

**REMARK:** The current release does not support the additional
features concerning the reminder messages, since that requires
a lot of assumptions about how to fugure out who to remind
about other persons birthday. It may require additional data
(e.g. a list of subscribers for reminders).

**REMARK:** The Feb, 29th feature will be covered.

## Core Functionality
```plantuml
actor "Caller" as CALLER

rectangle "Birthday Greeting" as GREETER {

    usecase RUN [
    Run new 
    birthday greeter.
    ]
    CALLER ->> RUN
    
    usecase "Get current date." as TODATE
    note "Obey leap years." as NOTE1
    RUN ..> TODATE : include
    TODATE .. NOTE1
    
    usecase SELECT [
    Select todays
    persons to greet
    from the database.
    ]
    RUN ..> SELECT : include
    note "Obey Feb29 if not leap year." as NOTE2
    SELECT .. NOTE2

    usecase GREET [
    Greet persons,
    if any, for today.
    ]
    RUN <.. GREET : extends
}
```

## Application Dependencies
```plantuml
actor "Caller" as CALLER

package "Birthday Greeting" {
    rectangle "Birthday Greeting Application" as CORE
    CALLER ->> CORE
    rectangle " POJOs & Helpers" as HELPERS
    CORE ->> HELPERS
    rectangle "Friends List" as FRIENDS_IFS <<interface>>
    rectangle "Notification Service" as NOTIFICATOR_IFS <<interface>>
    CORE -->> FRIENDS_IFS
    CORE -->> NOTIFICATOR_IFS
}

package "Data Providers" {
    rectangle FRIENDS [
    Actual Data
    Provider
    ]
}

package "Notification Services" {
    rectangle NOTIFICATOR [
    Actual Notification
    Service Adapter
    ]
}
FRIENDS_IFS <|-- FRIENDS
NOTIFICATOR_IFS <|-- NOTIFICATOR
```

## Implementation
* The Birthday Greeting core shall be implemented with a
single non-default constructor that allows dependency
injection for the friends list, and notification service.
* The dependencies shall be provided as interfaces, not as
actual classes. For tests, mocks shall be implemented, as
part of the test code, and shall contain data, or receive
notification results, only for test purposes.
* Actual implementations of the data provider and
notification service can be developed later, and can be
tested and provided separately.
* Only the interfaces shall be part of the Birthday Greeting
implementation package. Actual peripheral components must
follow the Greeting application's interface definitions.

[(TOP)](#design-strategy)
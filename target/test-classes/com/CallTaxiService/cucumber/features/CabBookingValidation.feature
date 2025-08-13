Feature: Booking Page UI and Functional Validation

@success
Scenario: Submit form with all valid data
	Given User launches the browser and opens the cab booking page
	When User fills the form with valid details:
	| Name     | Phone      | Email         | Trip | Cab  | CabType | Date       | Time  | Passenger | TripType |
	| Atul | 9876543210 | atul@gmail.com | long | Mini | AC      | 2025-07-28 | 09:30 | 2         | oneway   |
	And User clicks on Book Now button
	Then Booking confirmation message "Your Booking has been Confirmed" should be displayed

@errordetected
Scenario: Submit form with missing name
	Given User launches the browser and opens the cab booking page
	When User fills the form with valid details:
	| Name | Phone      | Email         | Trip | Cab  | CabType | Date       | Time  | Passenger | TripType |
	|      | 9876543210 | atul@gmail.com | long | Mini | AC      | 2025-07-28 | 09:30 | 2         | oneway |
	And User clicks on Book Now button
	Then Error message "Please enter the name" should be shown under Name field

@knownbug
Scenario: Submit form with invalid email format
	Given User launches the browser and opens the cab booking page
	When User fills the form with valid details:
	| Name  | Phone      | Email        | Trip | Cab  | CabType | Date       | Time  | Passenger | TripType |
	| Atul | 9876543210 | gmail.com | long | Mini | AC      | 2025-07-28 | 09:30 | 2         | oneway |
	And User clicks on Book Now button
	Then Error message "Please enter valid email" should be shown under Email field

@errordetected
Scenario: Submit form with missing trip type
	Given User launches the browser and opens the cab booking page
	When User fills the form with valid details:
	| Name  | Phone      | Email          | Trip | Cab  | CabType | Date       | Time  | Passenger | TripType |
	| Rohit | 9876543210 | rohit@gmail.com |      | Mini | AC      | 2025-07-28 | 09:30 | 2         | oneway |
	And User clicks on Book Now button
	Then Error message "Please Select the Trip" should be shown under Trip selection

@errordetected
Scenario: Submit form with missing number of passengers
	Given User launches the browser and opens the cab booking page
	When User fills the form with valid details:
	| Name | Phone      | Email         | Trip  | Cab  | CabType | Date       | Time  | Passenger | TripType |
	| Sumit| 9876543210 | sumit@gmail.com | local | Mini | AC      | 2025-07-28 | 09:30 |           | oneway |
	And User clicks on Book Now button
	Then Error message "Please Select the number of passengers" should be shown under Passenger count

@exceldata
Scenario: Booking with row 0 from Excel data
	Given User reads booking data from Excel sheet "BookingData" and row 0
	When User fills the form using Excel data
	And User clicks on Book Now button
	Then Booking confirmation message "Your Booking has been Confirmed" should be displayed

@exceldata
Scenario: Booking with row 1 from Excel data
	Given User reads booking data from Excel sheet "BookingData" and row 1
	When User fills the form using Excel data
	And User clicks on Book Now button
	Then Email error message "Please enter the email" should be displayed

@errordetected
Scenario: Validate Phone Number field with invalid inputs
  Given User launches the browser and opens the cab booking page
  When User fills the form with valid details:
    | Name  | Phone      | Email          | Trip | Cab  | CabType | Date       | Time  | Passenger | TripType |
    | John Doe | INVALID    | john.doe@mail.com | long | Mini | AC      | 2025-07-28 | 09:30 | 2         | oneway   |
  And User clicks on Book Now button
  Then Error message "Invalid Phone Number" should be shown under Phone field

@errordetected
Scenario: Validate Pickup Date field with invalid date
  Given User launches the browser and opens the cab booking page
  When User fills the form with valid details:
    | Name  | Phone      | Email          | Trip | Cab  | CabType | Date       | Time  | Passenger | TripType |
    | Jane Doe | 9876543210 | jane.doe@mail.com | long | Mini | AC      | 2024-01-01 | 09:30 | 2         | oneway   |
  And User clicks on Book Now button
  Then Error message "Pickup date should be valid" should be shown under Pickup Date field

@errordetected
Scenario: Validate invalid time input 
  Given User launches the browser and opens the cab booking page
  When User fills the form with valid details:
    | Name  | Phone      | Email          | Trip | Cab  | CabType | Date       | Time       | Passenger | TripType |
    | Peter Pan | 9876543210 | peter.pan@mail.com | long | Mini | AC      | 2025-07-28 | 99:99      | 2         | oneway   |
  And User clicks on Book Now button
  Then Error message "Invalid time format" should be shown under Pickup Time field

@errordetected
Scenario: Validate Trip Type invalid selection 
  Given User launches the browser and opens the cab booking page
  When User fills the form with valid details:
    | Name  | Phone      | Email          | Trip | Cab  | CabType | Date       | Time  | Passenger | TripType   |
    | Alice   | 9876543210 | alice@mail.com | long | Mini | AC      | 2025-07-28 | 09:30 | 2         |          |
  And User clicks on Book Now button
  Then Error message "Error Message for Trip Type Field" should be shown under Trip Type field
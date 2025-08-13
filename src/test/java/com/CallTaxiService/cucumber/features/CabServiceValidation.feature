Feature: Service Page Cab Type Link Navigation
@nav
Scenario: Verify that cab service link navigates to the correct page
	Given User opens the Services page
	When User clicks on the "<Cab Type>" service link
	Then The user should be navigated to "<Target Page>"

Examples:
  | Cab Type | Target Page   |
  | Mini     | mini.html     |
  | Micro    | micro.html    |
  | Sedan    | sedan.html    |
  | Suv      | suv.html      |
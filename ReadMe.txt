TicketService Application

Commands 

Build: mvn package

Clean & Install : mvn clean install -U

execute test cases: mvn test

Assumptions:

1.) Class TicketsConfig is a snapshot of the whole system required for further provisioning of seat hold and reservation requests.
2.) It shall only keep track of reserved seats but not the actual reservation.
3.) All of SeatHold data is maintained in memory until it enters into resreved state or cleaned up by future task or thread. Implementation of this thread is not provided and assumed shall exactly do rverse of seta hold request and modify only the live objects and this thread does not affect persitent storage system.
4.) TicketsConfig is a singleton class so that any number of requests look at only one snapshot. Any changes to this class data need to acquire a lock to avoid race conditions.
5.) Loaded TicketsCOnfig with the data provided, in rela time shoudl be loaded for a persistent system or look up into another application.
6.) Did not hard code venue levels and kept open so as to be more robust for any venues with more or less levels.


Scope for improvement:
1.) Use of mocking testing frameworks.
2.) Implement the actual thread to clean up the seta hold objects after required time in seconds.




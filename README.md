# README #

### Food ordering system overview ###
![Food ordering system overview](.docs/food-ordering.png)

### Order state transitions ###
![Order state transitions](.docs/order-state-transitions.png)

### SAGA and Outbox ###
The SAGA pattern is a design pattern used in distributed systems and microservices architecture to manage and coordinate complex, long-running transactions or business processes. It helps maintain data consistency across multiple services by breaking down a large transaction into smaller, discrete steps or actions.
<p>
The Outbox Pattern, also known as the Transactional Outbox Pattern, is a design pattern used in distributed systems to improve data consistency and communication between services. It addresses the challenges of maintaining data integrity and ensuring reliable communication between microservices in an asynchronous manner.

#### Successful flow
![Outbox successful flow](.docs/outbox-happy-flow.png)
#### Payment failure flow
![Outbox payment failure flow](.docs/outbox-payment-failure.png)
#### Restaurant approval failure flow
![Outbox restaurant approval failure flow](.docs/outbox-approval-failure.png)


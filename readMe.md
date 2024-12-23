## Background
This Application simulates an ATM (Automated teller machine).

## Commands

* `login [name]` - Logs in as this customer and creates the customer if not exist

* `deposit [amount]` - Deposits this amount to the logged in customer

* `withdraw [amount]` - Withdraws this amount from the logged in customer

* `transfer [target] [amount]` - Transfers this amount from the logged in customer to the target customer

* `logout` - Logs out of the current customer

* `exit` - exits CLI , can start new using `start.sh`
## PreRequisite
If you have cloned the project newly run `./gradlew clean build` to download necessary dependencies.

## Run the App
The Application has a `main` class called `AtmSimulatorApplication` . This is starting point for application.

Once Updated input , use `./start.sh` task to execute the application.

## Run the tests
To run all the tests in application use gradle task `./gradlew test`

## Sample input and output
Tip : use command `exit` from CLI

```bash

$ login Alice

Hello, Alice!

Your balance is $0



$ deposit 100

Your balance is $100



$ logout

Goodbye, Alice!



$ login Bob

Hello, Bob!

Your balance is $0



$ deposit 80

Your balance is $80



$ transfer Alice 50

Transferred $50 to Alice

your balance is $30



$ transfer Alice 100

Transferred $30 to Alice

Your balance is $0

Owed $70 to Alice



$ deposit 30

Transferred $30 to Alice

Your balance is $0

Owed $40 to Alice



$ logout

Goodbye, Bob!



$ login Alice

Hello, Alice!

Your balance is $210

Owed $40 from Bob



$ transfer Bob 30

Your balance is $210

Owed $10 from Bob



$ logout

Goodbye, Alice!



$ login Bob

Hello, Bob!

Your balance is $0

Owed $10 to Alice



$ deposit 100

Transferred $10 to Alice

Your balance is $90



$ logout

Goodbye, Bob!

```
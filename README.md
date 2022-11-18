# Digital Services Tax Stub

## About
The Digital Services Tax (DST) digital service is split into a number of different microservices all serving specific functions which are listed below:

**Frontend** - The main frontend for the service which includes the pages for registration, returns and an account home page.

**Backend** - The service that the frontend uses to call HOD APIs to retrieve and send information relating to business information and subscribing to regime.

**Stub** - Microservice that is used to mimic the DES APIs when running services locally or in the development and staging environments.

This is the stub, it mimics the behaviour of the the HOD's used in the service. 

For details about the digital services tax see [the GOV.UK guidance](https://www.gov.uk/government/consultations/digital-services-tax-draft-guidance)

## APIs

**POST       /registration/organisation/utr/:utr**
Stub of the rosmLookupWithId call. A utr is required.

**POST       /registration/02.00.00/organisation**
Mocks rosmLookupWithoutID, returns a INVALID_PAYLOAD is the regime does not match DST.

**POST       /cross-regime/subscription/:regime/:idType/:idNumber**
Handles generic subscription api response for DST registration. 

**POST /cross-regime/return/DST/eeits/:dstRegNo**
Returns a processsingDate and formBundleNumber in response to a DST return.

**GET /enterprise/obligation-data/zdst/:dstRegNo/DST**
Returns DST return periods.

**GET /trigger/callback/te/:seed**
Allows for manual tax enrolments callback, the seed can be utr or safId, they must equal what was used in registration.

**GET /get-subscription/:seed** 
Retrieves a generated DST subscription.

**GET /enterprise/financial-data/ZDST/:dstRegNo/DST**
Displays canned financial data

## Running from source
Clone the repository using SSH:

`git@github.com:hmrc/digital-services-tax-stub.git`

If you need to setup SSH, see [the github guide to setting up SSH](https://help.github.com/articles/adding-a-new-ssh-key-to-your-github-account/)

Run the code from source using 

`sbt run`

Open your browser and navigate to the following url:

`http://localhost:8740/digital-services-tax-frontend/`

## Running through service manager

Run the following command in a terminal: `nano /home/<USER>/.sbt/.credentials`

See the output and ensure it is populated with the following details:

```
realm=Sonatype Nexus Repository Manager
host=NEXUS URL
user=USERNAME
password=PASSWORD
```

*You need to be on the VPN*

Ensure your service manager config is up to date, and run the following command:

`sm --start DST_ALL -f`

This will start all the required services

## Running scalafmt

To apply scalafmt formatting using the rules configured in the .scalafmt.conf, run:

`sbt scalafmtAll`

To check the files have been formatted correctly, run:

`sbt scalafmtCheckAll`

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")
 
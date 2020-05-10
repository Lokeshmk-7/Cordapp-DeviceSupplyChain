# Medical Device/Instrument Market - TokenSDK

## Introduction 
This sample Cordapp demonstrate some simple flows related to the token SDK. In this Cordapp,there are four parties: 
- The Pharma Company (PharmaCo): can manufacture, sell, and recall/total the devices.
- The Depo: Buy the devices from the PharmaCo
- Used Devices Agency: Buy used devices from the Depo(or end-Customers)
- Customer: Buy devices from the PharmaCo or Depo, or buy used devices from used devices agency. 

In this sample Cordapp, we will mimic a medical devices buying and selling market. 

This device buying/selling market is capable of mimicing multiple business logics. We will be demonstrating one of the possible logic here:
1. PharmaCo manufactures the devices
2. PharmaCo can sell the device to depo and customers. 
3. Used devices agency can get the used device from the depo or customers. 
4. When there is a need of total the device, the current of the physical device will redeem the token with the PharmaCo

Throughout the sample, we will see how to create, transacte, and redeem a token. 

## Running the sample(on windows)
Deploy and run the nodes by:
```
Gradlew clean deployNodes
./build/nodes/runnodes
```
if you have any questions during setup, please go to https://docs.corda.net/getting-set-up.html for detailed setup instructions.

Once all four nodes are started up, in PharmaCo's node shell, run: 
```
flow start CreateDeviceFlow name: Microscope, batchno: SRN1, dom: 2019, amount: 50000
```
After this step, we have created a token representing the physical device with unique name(which we assume will be unique in the manufacturing). 
Then run:
```
flow start IssueDeviceFlow name: Microscope, batchno: SRN1, dom: 2016, amount: 50000, holder: Depo
```
This line of command will transfer the token to the depo. 

Now, at the depo’s shell, we can see we did recieve the token by running: 
```
run vaultQuery contractStateType: com.r3.corda.lib.tokens.contracts.states.NonFungibleToken
```
Continue to the business flow, the depo will sell the bike to the Customer. Run: 
```
flow start TransferDeviceFlow name: Microscope, batchno: SRN1, dom: 2016, amount: 50000, holder: Customer
```

Now we can check at the Customer's node shell to see if the Customer recieves the token by running the same `vaultQuery` we just ran at the depo's shell. 

At the Customer side, we would assume we got a recall notice and will send the physical device back to the manufacturer. The action will happen in real life, but on the ledger we will also need to "destroy"(process of redeem in Corda TokenSDK) the device token. Run:
```
flow start TotalPart name: Microscope
```
At the Customer 's shell, if we do the `vaultQuery` again, we will see that the device token is gone. 

Similarly, we can sell the device to the used device agency. We will achieve it by running: 
```
flow start TransferDeviceFlow name: Microscope, batchno: SRN1, dom: 2016, amount: 50000, holder: UsedDevicesAgency 
```






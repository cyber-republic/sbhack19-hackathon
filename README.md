# Elastonians at SBHACK19
## Swiss Blockchain Hackathon Trust Square 21.-23. June 2019

The approach to solving challenges in the Supply Chain vertical is the combination of the Internet-of-Things (IoT), Peer-to-Peer communication and Blockchain.

The challenges are:
- Challenge 2: Create a solution that ensures traceability and authentication of unique products within the pharma supply chain.
- Challenge 3: Guarantee traceability and authentication without divulging confidential product information with a public blockchain.

While monitoring all conditions of cargo during transport generates large volumes of data, it must be exchanged in real-time securely and permission based.

By using peer-to-peer communication inside an Internet of Things framework, these processes may be entirely automated without having to publishing confidential information to a shared ledger or store it in a centralized manner.

On top, Decentralized Identities, also called DIDs, are assigned on a public ledger to every product and actor involved in the supply chain.
Eventually, only the critical states of the product are written on the DIDs along its journey.

This way, permission based real-time data exchange is enabled, while public traceability also becomes possible.

## Why the combination of IoT, Peer-to-Peer communication and Blockchain?
- Minimize data written on the blockchain
- Protect sensitive data
- Allow permissioned real-time shipment monitoring
- Allow public proof of origin for products to consumers
- Keep entire ecosystem peer-to-peer and accessible to anyone
- Reduce infrastructure costs
- Reduce human errors


## Internet-of-Things and Peer-to-Peer communication

#### Features
- **Peer-to-Peer communication**
- **Graphical User Interface**
- **Decentralized Identity management** on Blockchain
- **Private or permissioned data storage**
- Permissioned access
- Connection management
- Event management (Rules Engine)
- Notifications
- Global Remote Access
- Bidirectional communication
- Real-time data
- Data history
- IoT Sensor management **
- Data aggregation and processing (averaging) **
- File-sharing (Image, Video, Audio) **
- Machine Learning integration (for faulty data elimination)**
** Not extensive / available in demonstration

#### Benefits
- **No third-party dependence**
- **No network cost**
- **Highly secure**
- Easy to use
- Indefinitely scalable
- Hardware agnostic
- Fully open source
- Customizable



## Blockchain

#### Features
- **Decentralized Identities (DIDs)**
- Public status history
- DIDs available for any product, shipment, user (actor), etc.
- DID to DID references

#### Benefits
- **Highly secure** - merge-mined with Bitcoin
- **Free identity creation and status update**
- Allows semi-anonymity of actors
- Tamperproof



## How does it work?

#### Quick Overview
The solution consists of two software components:
1. IoT Gateway
2. Mobile Application (currently Android)

- Both applications communicate via a peer-to-peer network and are nodes themselves.
- Each node has a unique Address which allows the establishment of the connection and bi-directional communication.
- Nodes automatically connect to the peer-to-peer network on startup.
- Data is end-to-end encrypted and transmitted via the peer-to-peer network. Only the nodes store data locally, the network never stores data (ever).
- The mobile application manages decentralized identities by interacting with the blockchain.


#### Pre-requisites
- The container must have the IoT gateway installed (hardware and software). This is a one-time setup.
- The users of the container must install the mobile application on mobile phones.


#### Process
1. The user must start the mobile application, click "Add Container", assign a descriptive name and scan the QR code available on the display of the container.
2. The connection is automatically accepted by the container (for demonstration, in production a manual approval is necessary).
3. When the container appears as "Online" on the mobile application, the user can start loading the container.
4. By clicking the 'load container' button, the camera will be activated and the products may be scanned.
5. By scanning a product, the DID of the scanned products will be listed and stored.
6. To set a range of accepted values, the fields under "Conditions" must be filled. The rules engine will automatically monitor the values and react to changes accordingly.
7. To finalize the loading process, the  "Seal Container" button must be clicked. The DID address of the container is then recorded on the DID of the products, making a reference back to the DID of the container with a timestamp. Also, the container DID records the "sealed" status on the DID blockchain.
8. In the background, the IoT data collection starts once the container is sealed and the user starts receiving live data from the container. This can be monitored from the mobile application.
9. With the rules engine running in the background, if the limits are overstepped, the system will automatically and instantly record the status "DAMAGED" on the blockchain.
10. If the container is kept in optimal conditions, it may be stored or transported. The status "storage" is automatically assigned and assumes no movement of the container.
11. To start the transport of a container, the button "Start Transport" must be clicked. The status "sender - ok" will be recorded on the DID of the container.
12. Again, with the rules engine running in the background, if the limits are overstepped, the system will automatically and instantly record the status "DAMAGED" on the blockchain.
13. The recipient of the container must start the mobile application on his/her phone and click "Receive Container". A descriptive name must be added in the field and the QR code of the container must be scanned.
14. The status "recipient - ok" will be recorded on the DID blockchain of the container.
15. The initial user (sender) now loses direct access to the container data as the transport is now final. The new user has now full control over the container.
16. This cycle may be repeated indefinitely.


#### Aspects
- Damage of the container is always instantly recorded on the blockchain.
- Products with DIDs always refer to the DID of the container in which they were loaded and because of the timeframe, the product may be traced back to its origin.
- The product's DID can only be updated with a private key which is only available to the current user. This data transfer is managed automatically by the mobile application.


## Demonstration
Coming soon.

## Used Public Libraries

#### IoT Gateway
- [Elastos Native Carrier SDK](https://github.com/elastos/Elastos.NET.Carrier.Native.SDK)
- [Elastos Java Carrier SDK](https://github.com/elastos/Elastos.NET.Carrier.Java.SDK)

#### Mobile Application
- [Elastos Android Carrier SDK](https://github.com/elastos/Elastos.NET.Carrier.Android.SDK)
- [Elastos DID Client SDK](https://github.com/elastos/Elastos.SDK.DIDClient.Java)


## License
This project is licensed under the terms of the [MIT License](https://github.com/cyber-republic/sbhack19-hackathon/blob/master/LICENSE).

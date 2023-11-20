CREATE TABLE ProductTypes
(
    name TEXT not null
        primary key
);

CREATE TABLE Games
(
    name        TEXT not null
        primary key,
    releaseDate TEXT
);

CREATE TABLE Products
(
    id    integer not null
        primary key autoincrement,
    name  TEXT    not null,
    type  TEXT    not null
        references ProductTypes (name)
            deferrable initially deferred,
    game  TEXT    not null
        references Games (name)
            deferrable initially deferred,
    price INTEGER not null
);

CREATE TABLE Images
(
    id   integer not null
        primary key autoincrement,
    data BLOB    not null
);

CREATE TABLE Users
(
    username TEXT not null
        primary key,
    passhash TEXT not null, 
    salt TEXT not null
);

CREATE TABLE ProductImages
(
    productID INTEGER not null
        references Products
            deferrable initially deferred,
    imageID   INTEGER not null
        references Images
            deferrable initially deferred,
    isPrimary INTEGER not null,
    primary key (productID, imageID),
    check (isPrimary IN (0, 1))
);

CREATE UNIQUE INDEX ProductImagesPrimaryIndex
    on ProductImages (productID)
    where isPrimary = 1;

CREATE TABLE ProductReviews
(
    id        INTEGER not null
        primary key autoincrement,
    productID INTEGER not null
        references Products
            deferrable initially deferred,
    review    TEXT,
    rating    INTEGER not null,
    check (rating IN (0, 1, 2, 3, 4, 5))
);

CREATE TABLE CartItems
(
    username  TEXT              not null
        references Users
            deferrable initially deferred,
    productID INTEGER           not null
        references Products
            deferrable initially deferred,
    quantity  INTEGER default 1 not null,
    primary key (username, productID),
    check (quantity > 0)
);

CREATE TABLE Addresses
(
    id         integer not null
        primary key autoincrement,
    street1    TEXT    not null,
    street2    TEXT,
    locality   TEXT    not null,
    province   TEXT    not null,
    country    TEXT    not null,
    planet     TEXT    not null,
    postalCode TEXT
);

CREATE TABLE ShippingMethods
(
    name TEXT not null
        primary key, 
    cost integer not null
);

CREATE TABLE OrderStatuses
(
    name TEXT not null
        primary key
);

CREATE TABLE UserAddresses
(
    username  integer TEXT not null
        references Users (username)
            deferrable initially deferred,
    addressID integer      not null
        references Addresses (id)
            deferrable initially deferred,
    name TEXT 		   not null,
    isPrimary integer      not null, 
    primary key (username, addressID),
    check (isPrimary IN (0, 1))
);

CREATE UNIQUE INDEX UserAddressesPrimaryIndex
    on UserAddresses (username)
    where isPrimary = 1;

CREATE UNIQUE INDEX UserAddressesNameIndex
    on UserAddresses (username, name);

CREATE TABLE PaymentMethods
(
    id        integer not null
        primary key autoincrement,
    username  TEXT    not null
        references Users (username),
    name      TEXT    not null,
    number    TEXT    not null,
    isPrimary integer not null,
    unique (id, username),
    check (isPrimary IN (0, 1))
);

CREATE UNIQUE INDEX PaymentMethodsNameIndex
    on PaymentMethods (username, name);

CREATE UNIQUE INDEX PaymentMethodsNumberIndex
    on PaymentMethods (username, number);

CREATE UNIQUE INDEX PaymentMethodsUsernameIndex
    on PaymentMethods (username)
    where isPrimary = 1;

CREATE TABLE Orders
(
    id              integer not null
        primary key autoincrement,
    username        TEXT    not null
        references Users (username)
            deferrable initially deferred,
    addressID       integer not null,
    shippingMethod  TEXT    not null
        references ShippingMethods (name)
            deferrable initially deferred,
    paymentMethodID integer not null,
    createdAt       TEXT default (strftime('%Y-%m-%dT%H:%M:%SZ')) not null,
    foreign key (username, addressID) references UserAddresses (username, addressID)
        deferrable initially deferred,
    foreign key (paymentMethodID, username) references PaymentMethods (id, username)
        deferrable initially deferred,
    check (createdAt IS strftime('%Y-%m-%dT%H:%M:%SZ', createdAt))
);

CREATE TABLE OrderItems
(
    orderID   integer not null
        references Orders (id)
            deferrable initially deferred,
    productID integer not null
        references Products (id)
            deferrable initially deferred,
    quantity  integer not null,
    primary key (orderID, productID),
    check (quantity > 0)
);

CREATE TABLE OrderActivities
(
    id      integer not null
        primary key autoincrement,
    orderID integer not null
        references Orders (id)
            deferrable initially deferred,
    status  TEXT    not null
        references OrderStatuses (name)
            deferrable initially deferred,
    date    TEXT default (strftime('%Y-%m-%dT%H:%M:%SZ')) not null,
    check (date IS strftime('%Y-%m-%dT%H:%M:%SZ', date))
);

INSERT INTO Users VALUES('mother_brain','$2a$10$NRDEFHbq5heYWI8LLI0rO8jUJmJMlDL0Lpmpq6ZwbJ8cnUvOIEXa','U7HvU1Uc');

INSERT INTO ProductTypes VALUES('Capacity Expansion');
INSERT INTO ProductTypes VALUES('Beam');
INSERT INTO ProductTypes VALUES('Weapon');
INSERT INTO ProductTypes VALUES('Suit Upgrade');
INSERT INTO ProductTypes VALUES('Mobility');

INSERT INTO Games VALUES('Super Metroid','1994-03-19');
INSERT INTO Games VALUES('Metroid: Zero Mission','2004-02-09');

INSERT INTO Products VALUES(1,'Missile Expansion','Capacity Expansion','Super Metroid',999);
INSERT INTO Products VALUES(2,'Charge Beam','Beam','Super Metroid',9999);
INSERT INTO Products VALUES(3,'Morph Ball','Mobility','Super Metroid',4999);
INSERT INTO Products VALUES(4,'Bomb','Weapon','Super Metroid',4999);
INSERT INTO Products VALUES(5,'High Jump Boots','Mobility','Super Metroid',3999);

INSERT INTO Images VALUES(1,X'89504e470d0a1a0a0000000d49484452000000100000001008060000001ff3ff6100000183694343504943432070726f66696c65000028917d913d48c3401cc55fd34a452b0e761071c8509d2c888a386a158a5021d40aad3a985cfa054d1a92141747c1b5e0e0c762d5c1c5595707574110fc0071757152749112ff97145ac47870dc8f77f71e77ef00a151619a151a0734dd36d3c98498cdad8ae157f42202200441669631274929f88eaf7b04f87a17e759fee7fe1c7d6ade624040249e658669136f104f6fda06e77de2282bc92af139f1984917247ee4bae2f11be7a2cb02cf8c9a99f43c7194582c76b0d2c1ac646ac453c43155d3295fc87aac72dee2ac556aac754ffec2485e5f59e63acd6124b188254810a1a086322ab011a75527c5429af6133efe21d72f914b2157198c1c0ba84283ecfac1ffe077b7566172c24b8a2480ae17c7f91801c2bb40b3ee38dfc78ed33c0182cfc095def6571bc0cc27e9f5b6163b02fab7818bebb6a6ec01973bc0e093219bb22b05690a8502f07e46df9403066e819e35afb7d63e4e1f800c7595ba010e0e81d12265affbbcbbbbb3b77fcfb4fafb01ba76725d77dbcd07000000097048597300000b1300000b1301009a9c180000000774494d4507e70b0c14072deb22ce2d0000001974455874436f6d6d656e74004372656174656420776974682047494d5057810e17000001164944415438cb9592a17ac3201485fff4aba8ec234c5e898c44564622232391957b84c8c9c9c84864642412d9c7c031d18575297c5b8f82cbb9e79e0340012292628c29c698dcea925b5d129154e236fb46ef3d004aa95fc4f17304c0f6961042539d2a22795a695d75b3353f0a86764ca11d9fea6e75797fd88a7b37f379e06d190098cf43919305bcf728a59eb2ddf4c793d31042637b9befe44805b7d6a2352ccb77c1499177a809601c5c22d6c97d5d41d581ed2dc67418d3617bfbba80311dd7eb3b8bbf6798a6f9c508ffc4b1f41f94524cd3cc2d0400561ff2d9e9746afe8c604c477bd168a5514a614c0790e3542388482a9100da8ba6f4e98e7be5c71b5fdd92a7ef5f6275cb8fc023719b5473b0356ef8026483980b41eb50a70000000049454e44ae426082');
INSERT INTO Images VALUES(2,X'89504e470d0a1a0a0000000d49484452000000100000001008060000001ff3ff6100000183694343504943432070726f66696c65000028917d913d48c3401cc55fd34a452b0e761071c8509d2c888a386a158a5021d40aad3a985cfa054d1a92141747c1b5e0e0c762d5c1c5595707574110fc0071757152749112ff97145ac47870dc8f77f71e77ef00a151619a151a0734dd36d3c98498cdad8ae157f42202200441669631274929f88eaf7b04f87a17e759fee7fe1c7d6ade624040249e658669136f104f6fda06e77de2282bc92af139f1984917247ee4bae2f11be7a2cb02cf8c9a99f43c7194582c76b0d2c1ac646ac453c43155d3295fc87aac72dee2ac556aac754ffec2485e5f59e63acd6124b188254810a1a086322ab011a75527c5429af6133efe21d72f914b2157198c1c0ba84283ecfac1ffe077b7566172c24b8a2480ae17c7f91801c2bb40b3ee38dfc78ed33c0182cfc095def6571bc0cc27e9f5b6163b02fab7818bebb6a6ec01973bc0e093219bb22b05690a8502f07e46df9403066e819e35afb7d63e4e1f800c7595ba010e0e81d12265affbbcbbbbb3b77fcfb4fafb01ba76725d77dbcd07000000097048597300000b1300000b1301009a9c180000000774494d4507e70b0c140b1b882d14b80000001974455874436f6d6d656e74004372656174656420776974682047494d5057810e17000001264944415438cb8d53ab8ec4300c9c5405fd84c2858681f7098581850b0b0b0f162e5c18b8b030f03ea1d030b0d07fd1032be77c7d483b5225cb89ed9971ea7082b6f59bc622ec3417422877524a1061579f1512110020e75cf24404ef3d862120c6a4675b6d8b89087ddf83994b31008410f07c7ea3691ac4984a136646651968b14e57fa7bc4984a5cd9e9b6788fd7eba7c4c3f0e7458d0f90d25bb3a53fcf3372ce70d6bc755d70bb7d81880e12aeb6e0943e1121a5b736bb852b1f0e12bcf725693d685bbfed999c36b05009ca04c0b63fd741658df77b7768a21735b69fa278a0ef40ddb5865d6d46845d25c24e356bf1ba2ea7ef414db6f9dafc3045e7383e0e1773ce98a6a93c7335f69f89ebba601c1f87c9caaaebeed6c44d849d3b5bcda71061f70bef3bc3eee7302a630000000049454e44ae426082');
INSERT INTO Images VALUES(3,X'89504e470d0a1a0a0000000d49484452000000100000001008060000001ff3ff6100000183694343504943432070726f66696c65000028917d913d48c3401cc55fd34a452b0e761071c8509d2c888a386a158a5021d40aad3a985cfa054d1a92141747c1b5e0e0c762d5c1c5595707574110fc0071757152749112ff97145ac47870dc8f77f71e77ef00a151619a151a0734dd36d3c98498cdad8ae157f42202200441669631274929f88eaf7b04f87a17e759fee7fe1c7d6ade624040249e658669136f104f6fda06e77de2282bc92af139f1984917247ee4bae2f11be7a2cb02cf8c9a99f43c7194582c76b0d2c1ac646ac453c43155d3295fc87aac72dee2ac556aac754ffec2485e5f59e63acd6124b188254810a1a086322ab011a75527c5429af6133efe21d72f914b2157198c1c0ba84283ecfac1ffe077b7566172c24b8a2480ae17c7f91801c2bb40b3ee38dfc78ed33c0182cfc095def6571bc0cc27e9f5b6163b02fab7818bebb6a6ec01973bc0e093219bb22b05690a8502f07e46df9403066e819e35afb7d63e4e1f800c7595ba010e0e81d12265affbbcbbbbb3b77fcfb4fafb01ba76725d77dbcd07000000097048597300000b1300000b1301009a9c180000000774494d4507e70b0c140c2be1b5b2d30000001974455874436f6d6d656e74004372656174656420776974682047494d5057810e17000000f84944415438cbad93a195c3301044bffc0eb8812b206cb1790a08761b66314a01410a5305e1c22920dcd82c05b801b33d2047912cbf3be09bf7167824ed8cd6235843ac0e931685586503263bf83c03d01c6fc5c66c6deccda62a623f059f4af8b51b03304caa5175ec412c9e3af66f9923ff76d37c1b035065771b7bbc383c35874b17694f8d17179a24ae79ab47cb62d58bd3e14e18de1df5e262a5fb864935384810954f8ba993164ed6a8d6c4ebeae0b10cf961c2f72ff8da225f57c781bf0f170e5ae6dcc90ae97a6cd01c6f212462f3296f61f995c3f31cc35665a9124b3b768552cb4c3b7631074b33f37f49dcff1676bcc6ddf80101eed08dea4f7ff40000000049454e44ae426082');
INSERT INTO Images VALUES(4,X'89504e470d0a1a0a0000000d49484452000000100000001008060000001ff3ff6100000183694343504943432070726f66696c65000028917d913d48c3401cc55fd34a452b0e761071c8509d2c888a386a158a5021d40aad3a985cfa054d1a92141747c1b5e0e0c762d5c1c5595707574110fc0071757152749112ff97145ac47870dc8f77f71e77ef00a151619a151a0734dd36d3c98498cdad8ae157f42202200441669631274929f88eaf7b04f87a17e759fee7fe1c7d6ade624040249e658669136f104f6fda06e77de2282bc92af139f1984917247ee4bae2f11be7a2cb02cf8c9a99f43c7194582c76b0d2c1ac646ac453c43155d3295fc87aac72dee2ac556aac754ffec2485e5f59e63acd6124b188254810a1a086322ab011a75527c5429af6133efe21d72f914b2157198c1c0ba84283ecfac1ffe077b7566172c24b8a2480ae17c7f91801c2bb40b3ee38dfc78ed33c0182cfc095def6571bc0cc27e9f5b6163b02fab7818bebb6a6ec01973bc0e093219bb22b05690a8502f07e46df9403066e819e35afb7d63e4e1f800c7595ba010e0e81d12265affbbcbbbbb3b77fcfb4fafb01ba76725d77dbcd07000000097048597300000b1300000b1301009a9c180000000774494d4507e70b0c140d12a7ab0b9a0000001974455874436f6d6d656e74004372656174656420776974682047494d5057810e17000001924944415438cb8d93b14ee34010863f4729429792d6272131a9a04cba4b17ae235588c40be027801a9ec029692241aaf83a2c21b1e9ece28a4b95b9eadca6f41b8462bd8e1d8360a46d66f6ffe79f7f773c8a18febcdcf5cf045f7a000c4643e4f81800dd6e49620340a61bd2b562569107e055c1e95af94eb8bb661579ed6ae2ed755976cb74c3e3226a807b62eff6cf04b38236c051073a1dc8f22d7f5786e8f71280e0dc8212c48ea24a962922c251c7d6da55f634363c2d960c50eeba61adf37d1e8008aaf5315b40695ca61b803df836b0a79a2bc2615ad5e4e3226280eec100a71b980fedcbd034b9f51dd77f9cf43ead35089c613c1492fff5f8df0febb5af14dce7c19ea4202a7307517b05712e8bf02b9f9533dbceb626224d05ce7d47329d8c51551284a4004e27e31ad8614a0583ca1e00bcfd31f85dbb0b59be258d0dd3c998c3865e55cec5e86a67c93e763d595be04bfcecd53cb8185dedc2f98c343664aaf822f44743fc6eb750909316dbe88b10ce67bbe0fa8697f8d96b1f7e63d7e969b1249ccfec4e5cdf3454b87807e077b176aea5e5410000000049454e44ae426082');
INSERT INTO Images VALUES(5,X'89504e470d0a1a0a0000000d49484452000000100000001008060000001ff3ff6100000183694343504943432070726f66696c65000028917d913d48c3401cc55fd34a452b0e761071c8509d2c888a386a158a5021d40aad3a985cfa054d1a92141747c1b5e0e0c762d5c1c5595707574110fc0071757152749112ff97145ac47870dc8f77f71e77ef00a151619a151a0734dd36d3c98498cdad8ae157f42202200441669631274929f88eaf7b04f87a17e759fee7fe1c7d6ade624040249e658669136f104f6fda06e77de2282bc92af139f1984917247ee4bae2f11be7a2cb02cf8c9a99f43c7194582c76b0d2c1ac646ac453c43155d3295fc87aac72dee2ac556aac754ffec2485e5f59e63acd6124b188254810a1a086322ab011a75527c5429af6133efe21d72f914b2157198c1c0ba84283ecfac1ffe077b7566172c24b8a2480ae17c7f91801c2bb40b3ee38dfc78ed33c0182cfc095def6571bc0cc27e9f5b6163b02fab7818bebb6a6ec01973bc0e093219bb22b05690a8502f07e46df9403066e819e35afb7d63e4e1f800c7595ba010e0e81d12265affbbcbbbbb3b77fcfb4fafb01ba76725d77dbcd07000000097048597300000b1300000b1301009a9c180000000774494d4507e70b0c140e03e63678ab0000001974455874436f6d6d656e74004372656174656420776974682047494d5057810e17000000e64944415438cb8593bb1983300c847ff2517814ca8ce0d2235052660446c80894293d824b8d9032a3d03985517040c05558e6f4389d1b2a0c1db93ebf3e3467f7c0fe87e90922105389f561bd8fe9ff0cd0ee2a8ee4e93d038e98f624efd76f11b861e0717778bf12b59b2d623212d473fbe77c9844c4d0c0d2833023a3db8d125329d672819a6c8d729a40b7a1e43e2c2286198009f26182458baca498aa0d24f74b7e3982259ec60e3550c769eb4a300c964dabaafadb7615f56a9b6dc5ab71fa50fc21a32b5b18ba55a85fd5a5f2a11ea93855041aeb855938f581d57aadf412cad6ebfc02594275ec6b1dfa8b0000000049454e44ae426082');

INSERT INTO ProductImages VALUES(1,1,1);
INSERT INTO ProductImages VALUES(2,2,1);
INSERT INTO ProductImages VALUES(3,3,1);
INSERT INTO ProductImages VALUES(4,4,1);
INSERT INTO ProductImages VALUES(5,5,1);

INSERT INTO ProductReviews VALUES(1,1,NULL,5);
INSERT INTO ProductReviews VALUES(2,1,NULL,5);
INSERT INTO ProductReviews VALUES(3,1,NULL,5);
INSERT INTO ProductReviews VALUES(4,1,NULL,4);
INSERT INTO ProductReviews VALUES(5,1,NULL,5);
INSERT INTO ProductReviews VALUES(6,3,NULL,5);
INSERT INTO ProductReviews VALUES(7,3,NULL,3);

INSERT INTO Addresses VALUES(1,'123 Crumbling Ln',NULL,'North Atrium','Chozo Ruins','Chozo Ruins','Tallon IV',NULL);

INSERT INTO ShippingMethods VALUES('Fusion Drive Freight',1000);
INSERT INTO ShippingMethods VALUES('Subspace Rail',5000);

INSERT INTO OrderStatuses VALUES('Preparing');
INSERT INTO OrderStatuses VALUES('Shipped');
INSERT INTO OrderStatuses VALUES('Delivered');
INSERT INTO OrderStatuses VALUES('Delayed');
INSERT INTO OrderStatuses VALUES('Cancelled');

INSERT INTO UserAddresses VALUES('mother_brain',1,'Lair',1);

INSERT INTO PaymentMethods VALUES(1,'mother_brain','Planetary Fund','154734627674',1);
### step 326

create front end folder
open folder in terminal

```bash
 npm create vite@latest
```

output:

```
Need to install the following packages:
create-vite@5.5.3
Ok to proceed? (y)

> npx
> create-vite

√ Project name: ... react
√ Select a framework: » React
√ Select a variant: » JavaScript

Scaffolding project in D:\PROFESSIONAL Full Stack Developer\amigosCode\spring-boot-fullstack\frontend\react...

Done. Now run:

  cd react
  npm install
  npm run dev
  
  ➜  Local:   http://localhost:5173/
  ➜  Network: use --host to expose
  ➜  press h + enter to show help

```

### step 327

Delete the gitignore file in the React project folder and paste the code into the main gitignore file

### step 328

open src folder play by changing the files

### step 329

ctrl + c to stop the app
npm run dev - to run the app
npm run build to build production ready dist folder

### step 330

delete App.css
update Customer.jsx

```javascript
function App() {

    return <h1>Hello React</h1>
}

export default App

```

clear all data in index.css
delete the index.css import in main.jsx

```bash
npm run dev
```

### step 331

we are going to start basics of react

### step 332

Customer.jsx

```jsx
function App() {
    const brand = "Kedarnath";
    const counter = 0;
    return (

        <div>
            <p>{counter + 2}</p>
            <ol>
                <li>React</li>
                <li>Angular</li>
                <li>Vue</li>
                <li>Other</li>
            </ol>
        </div>
    )
}

export default App;
```

jsx is used to write to html and javascript in one file

### step 333

Topic: components

intro to components
refer https://react.dev/reference/react/Component

### step 334

refer https://randomuser.me/

create file under src UserProfile.jsx

```jsx
const UserProfile = () => {
    return (
        <div>
            <p>King</p>
            <img src={"https://randomuser.me/api/portraits/men/75.jpg"}/>
        </div>
    );
};

export default UserProfile;
```

update Customer.jsx

```jsx
import UserProfile from "./UserProfile";

function App() {
    const brand = "Kedarnath";
    const counter = 0;
    return (

        <div>
            <UserProfile/>
            <UserProfile/>
        </div>
    )
}

export default App;
```

### step 335

Customer.jsx:

```jsx
import UserProfile from "./UserProfile.jsx";

function App() {
    return (
        <div>
            <UserProfile
                name={"King"}
                age={21}
                gender={"men"}  // The randomuser API expects "men" or "women"
            />
            <UserProfile
                name={"Queen"}
                age={20}
                gender={"women"}
            />
        </div>
    );
}

export default App;

```

UserProfile.jsx

```jsx
const UserProfile = ({name, age, gender}) => {
    return (
        <div>
            <p>{name}</p>
            <p>{age}</p>
            <img src={`https://randomuser.me/api/portraits/${gender}/75.jpg`} alt="User portrait"/>
        </div>
    );
};

export default UserProfile;

```

React components always starts with capital letter

### step 336

Children:
Customer.jsx

```jsx
import UserProfile from "./UserProfile.jsx";

function App() {
    return (
        <div>
            <UserProfile
                name={"King"}
                age={21}
                gender={"men"}>
                <p>Hello</p>
            </UserProfile>
            <UserProfile
                name={"Queen"}
                age={20}
                gender={"women"}>
                <p>Hello</p>
            </UserProfile>

        </div>
    );
}

export default App;

```

UserProfile.jsx

```jsx
const UserProfile = ({name, age, gender, ...props}) => {
    return (
        <div>
            <p>{name}</p>
            <p>{age}</p>
            <img src={`https://randomuser.me/api/portraits/${gender}/75.jpg`} alt="User portrait"/>
            {props.children}
        </div>
    );
};

export default UserProfile;

```

### step 337

### step 338

### step 339

Customer.jsx:

```jsx
import UserProfile from "./UserProfile.jsx";
import {useEffect, useState} from "react";

const users = [
    {
        name: "king",
        age: 21,
        gender: "MALE"
    }, {
        name: "queen",
        age: 21,
        gender: "FEMALE"
    }, {
        name: "king servent",
        age: 30,
        gender: "MALE"
    }, {
        name: "queen servent",
        age: 30,
        gender: "FEMALE"
    }, {
        name: "part animal",
        age: 18,
        gender: "MALE"
    },
]

const UserProfiles = ({users}) => (
    <div>
        {users.map((user, index) => (
            <UserProfile
                key={index}
                name={user.name}
                age={user.age}
                gender={user.gender}
                imageNumber={index}

            />
        ))}
    </div>
)

function App() {
    const [counter, setCounter] = useState(0);
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        setIsLoading(true)
        setTimeout(() => {
            setIsLoading(false)
        }, 4000)
        return () => {
            console.log("cleanup functions")
        }
    }, [])
    if (isLoading) {
        return "loading..."
    }
    return (
        <div>
            <button onClick={() => setCounter(prevCounter => prevCounter + 1)}>
                Increment Counter
            </button>
            <h1>{counter}</h1>
            <UserProfiles users={users}/>
        </div>
    );
}

export default App;

```

### step 340

basic let start to connect the backend

### step 341

we dont like css so we ui libraries

### step 342

refer these
https://mui.com/material-ui/
https://v2.chakra-ui.com/getting-started
https://ant.design/
we are going to use chakra lets install in next steps

### step 343

install chakra

```bash
npm i @chakra-ui/react @emotion/react @emotion/styled framer-motion
```

upadate main.jsx

```jsx
import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import {ChakraProvider} from '@chakra-ui/react'
import App from './Customer.jsx'

createRoot(document.getElementById('root')).render(
    <StrictMode>
        <ChakraProvider>
            <App/>
        </ChakraProvider>
    </StrictMode>,
)

```

### step 344

delete everything from app.jsx

```jsx
import {Button, ButtonGroup} from '@chakra-ui/react'

const App = () => {
    return (
        <Button colorScheme='teal' variant='outline'>Click me</Button>
    )

}
export default App;
```

### step 345

refer:
https://chakra-templates.vercel.app/
https://react-icons.github.io/react-icons/

```bash
npm i react-icons
```

under src create directory shared
create file SideBar.jsx

SideBar.jsx:

```jsx
'use client'

import {
    IconButton,
    Avatar,
    Box,
    CloseButton,
    Flex,
    HStack,
    VStack,
    Icon,
    useColorModeValue,
    Text,
    Drawer,
    DrawerContent,
    useDisclosure,
    Menu,
    MenuButton,
    MenuDivider,
    MenuItem,
    MenuList,
} from '@chakra-ui/react'
import {
    FiHome,
    FiTrendingUp,
    FiCompass,
    FiStar,
    FiSettings,
    FiMenu,
    FiBell,
    FiChevronDown,
} from 'react-icons/fi'

const LinkItems = [
    {name: 'Home', icon: FiHome},
    {name: 'Trending', icon: FiTrendingUp},
    {name: 'Explore', icon: FiCompass},
    {name: 'Favourites', icon: FiStar},
    {name: 'Settings', icon: FiSettings},
]

const SidebarContent = ({onClose, ...rest}) => {
    return (
        <Box
            transition="3s ease"
            bg={useColorModeValue('white', 'gray.900')}
            borderRight="1px"
            borderRightColor={useColorModeValue('gray.200', 'gray.700')}
            w={{base: 'full', md: 60}}
            pos="fixed"
            h="full"
            {...rest}>
            <Flex h="20" alignItems="center" mx="8" justifyContent="space-between">
                <Text fontSize="2xl" fontFamily="monospace" fontWeight="bold">
                    Logo
                </Text>
                <CloseButton display={{base: 'flex', md: 'none'}} onClick={onClose}/>
            </Flex>
            {LinkItems.map((link) => (
                <NavItem key={link.name} icon={link.icon}>
                    {link.name}
                </NavItem>
            ))}
        </Box>
    )
}

const NavItem = ({icon, children, ...rest}) => {
    return (
        <Box
            as="a"
            href="#"
            style={{textDecoration: 'none'}}
            _focus={{boxShadow: 'none'}}>
            <Flex
                align="center"
                p="4"
                mx="4"
                borderRadius="lg"
                role="group"
                cursor="pointer"
                _hover={{
                    bg: 'cyan.400',
                    color: 'white',
                }}
                {...rest}>
                {icon && (
                    <Icon
                        mr="4"
                        fontSize="16"
                        _groupHover={{
                            color: 'white',
                        }}
                        as={icon}
                    />
                )}
                {children}
            </Flex>
        </Box>
    )
}

const MobileNav = ({onOpen, ...rest}) => {
    return (
        <Flex
            ml={{base: 0, md: 60}}
            px={{base: 4, md: 4}}
            height="20"
            alignItems="center"
            bg={useColorModeValue('white', 'gray.900')}
            borderBottomWidth="1px"
            borderBottomColor={useColorModeValue('gray.200', 'gray.700')}
            justifyContent={{base: 'space-between', md: 'flex-end'}}
            {...rest}>
            <IconButton
                display={{base: 'flex', md: 'none'}}
                onClick={onOpen}
                variant="outline"
                aria-label="open menu"
                icon={<FiMenu/>}
            />

            <Text
                display={{base: 'flex', md: 'none'}}
                fontSize="2xl"
                fontFamily="monospace"
                fontWeight="bold">
                Logo
            </Text>

            <HStack spacing={{base: '0', md: '6'}}>
                <IconButton size="lg" variant="ghost" aria-label="open menu" icon={<FiBell/>}/>
                <Flex alignItems={'center'}>
                    <Menu>
                        <MenuButton py={2} transition="all 0.3s" _focus={{boxShadow: 'none'}}>
                            <HStack>
                                <Avatar
                                    size={'sm'}
                                    src={
                                        'https://images.unsplash.com/photo-1619946794135-5bc917a27793?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=200&w=200&s=b616b2c5b373a80ffc9636ba24f7a4a9'
                                    }
                                />
                                <VStack
                                    display={{base: 'none', md: 'flex'}}
                                    alignItems="flex-start"
                                    spacing="1px"
                                    ml="2">
                                    <Text fontSize="sm">Justina Clark</Text>
                                    <Text fontSize="xs" color="gray.600">
                                        Admin
                                    </Text>
                                </VStack>
                                <Box display={{base: 'none', md: 'flex'}}>
                                    <FiChevronDown/>
                                </Box>
                            </HStack>
                        </MenuButton>
                        <MenuList
                            bg={useColorModeValue('white', 'gray.900')}
                            borderColor={useColorModeValue('gray.200', 'gray.700')}>
                            <MenuItem>Profile</MenuItem>
                            <MenuItem>Settings</MenuItem>
                            <MenuItem>Billing</MenuItem>
                            <MenuDivider/>
                            <MenuItem>Sign out</MenuItem>
                        </MenuList>
                    </Menu>
                </Flex>
            </HStack>
        </Flex>
    )
}

const SidebarWithHeader = ({children}) => {
    const {isOpen, onOpen, onClose} = useDisclosure()

    return (
        <Box minH="100vh" bg={useColorModeValue('gray.100', 'gray.900')}>
            <SidebarContent onClose={() => onClose} display={{base: 'none', md: 'block'}}/>
            <Drawer
                isOpen={isOpen}
                placement="left"
                onClose={onClose}
                returnFocusOnClose={false}
                onOverlayClick={onClose}
                size="full">
                <DrawerContent>
                    <SidebarContent onClose={onClose}/>
                </DrawerContent>
            </Drawer>
            {/* mobilenav */}
            <MobileNav onOpen={onOpen}/>
            <Box ml={{base: 0, md: 60}} p="4">
                {children} {/* Content */}
            </Box>
        </Box>
    )
}

export default SidebarWithHeader

```

update the Customer.jsx:

```jsx
import {Button, ButtonGroup} from '@chakra-ui/react'
import SidebarWithHeader from "./shared/SideBar";

const App = () => {

    return (
        <SidebarWithHeader>
            <Button colorScheme='teal' variant='outline'>Click me</Button>
        </SidebarWithHeader>
    )

}
export default App;
```

### step 346

update sidebar.jsx

```jsx
'use client'

import {
    IconButton,
    Avatar,
    Box,
    CloseButton,
    Flex,
    HStack,
    VStack,
    Icon,
    useColorModeValue,
    Text,
    Drawer,
    DrawerContent,
    useDisclosure,
    Menu,
    MenuButton,
    MenuDivider,
    MenuItem,
    MenuList, Image,
} from '@chakra-ui/react'
import {
    FiHome,
    FiTrendingUp,
    FiCompass,
    FiStar,
    FiSettings,
    FiMenu,
    FiBell,
    FiChevronDown,
} from 'react-icons/fi'

const LinkItems = [
    {name: 'Home', icon: FiHome},
    {name: 'Trending', icon: FiTrendingUp},
    {name: 'Explore', icon: FiCompass},
    {name: 'Favourites', icon: FiStar},
    {name: 'Settings', icon: FiSettings},
]

const SidebarContent = ({onClose, ...rest}) => {
    return (
        <Box
            transition="3s ease"
            bg={useColorModeValue('white', 'gray.900')}
            borderRight="1px"
            borderRightColor={useColorModeValue('gray.200', 'gray.700')}
            w={{base: 'full', md: 60}}
            pos="fixed"
            h="full"
            {...rest}>
            <Flex h="20" flexDirection="column" alignItems="center" mx="8" mb={70} mt={2}
                  justifyContent="space-between">
                <Text fontSize="2xl" fontFamily="monospace" fontWeight="bold" mb={5}>
                    Dashboard
                </Text>
                <Image
                    borderRadius='full'
                    boxSize='75px'
                    src='https://bit.ly/dan-abramov'
                    alt='Dan Abramov'
                />
                <CloseButton display={{base: 'flex', md: 'none'}} onClick={onClose}/>
            </Flex>
            {LinkItems.map((link) => (
                <NavItem key={link.name} icon={link.icon}>
                    {link.name}
                </NavItem>
            ))}
        </Box>
    )
}

const NavItem = ({icon, children, ...rest}) => {
    return (
        <Box
            as="a"
            href="#"
            style={{textDecoration: 'none'}}
            _focus={{boxShadow: 'none'}}>
            <Flex
                align="center"
                p="4"
                mx="4"
                borderRadius="lg"
                role="group"
                cursor="pointer"
                _hover={{
                    bg: 'cyan.400',
                    color: 'white',
                }}
                {...rest}>
                {icon && (
                    <Icon
                        mr="4"
                        fontSize="16"
                        _groupHover={{
                            color: 'white',
                        }}
                        as={icon}
                    />
                )}
                {children}
            </Flex>
        </Box>
    )
}

const MobileNav = ({onOpen, ...rest}) => {
    return (
        <Flex
            ml={{base: 0, md: 60}}
            px={{base: 4, md: 4}}
            height="20"
            alignItems="center"
            bg={useColorModeValue('white', 'gray.900')}
            borderBottomWidth="1px"
            borderBottomColor={useColorModeValue('gray.200', 'gray.700')}
            justifyContent={{base: 'space-between', md: 'flex-end'}}
            {...rest}>
            <IconButton
                display={{base: 'flex', md: 'none'}}
                onClick={onOpen}
                variant="outline"
                aria-label="open menu"
                icon={<FiMenu/>}
            />

            <Text
                display={{base: 'flex', md: 'none'}}
                fontSize="2xl"
                fontFamily="monospace"
                fontWeight="bold">
                Logo
            </Text>

            <HStack spacing={{base: '0', md: '6'}}>
                <IconButton size="lg" variant="ghost" aria-label="open menu" icon={<FiBell/>}/>
                <Flex alignItems={'center'}>
                    <Menu>
                        <MenuButton py={2} transition="all 0.3s" _focus={{boxShadow: 'none'}}>
                            <HStack>
                                <Avatar
                                    size={'sm'}
                                    src={
                                        'https://images.unsplash.com/photo-1619946794135-5bc917a27793?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=200&w=200&s=b616b2c5b373a80ffc9636ba24f7a4a9'
                                    }
                                />
                                <VStack
                                    display={{base: 'none', md: 'flex'}}
                                    alignItems="flex-start"
                                    spacing="1px"
                                    ml="2">
                                    <Text fontSize="sm">Justina Clark</Text>
                                    <Text fontSize="xs" color="gray.600">
                                        Admin
                                    </Text>
                                </VStack>
                                <Box display={{base: 'none', md: 'flex'}}>
                                    <FiChevronDown/>
                                </Box>
                            </HStack>
                        </MenuButton>
                        <MenuList
                            bg={useColorModeValue('white', 'gray.900')}
                            borderColor={useColorModeValue('gray.200', 'gray.700')}>
                            <MenuItem>Profile</MenuItem>
                            <MenuItem>Settings</MenuItem>
                            <MenuItem>Billing</MenuItem>
                            <MenuDivider/>
                            <MenuItem>Sign out</MenuItem>
                        </MenuList>
                    </Menu>
                </Flex>
            </HStack>
        </Flex>
    )
}

const SidebarWithHeader = ({children}) => {
    const {isOpen, onOpen, onClose} = useDisclosure()

    return (
        <Box minH="100vh" bg={useColorModeValue('gray.100', 'gray.900')}>
            <SidebarContent onClose={() => onClose} display={{base: 'none', md: 'block'}}/>
            <Drawer
                isOpen={isOpen}
                placement="left"
                onClose={onClose}
                returnFocusOnClose={false}
                onOverlayClick={onClose}
                size="full">
                <DrawerContent>
                    <SidebarContent onClose={onClose}/>
                </DrawerContent>
            </Drawer>
            {/* mobilenav */}
            <MobileNav onOpen={onOpen}/>
            <Box ml={{base: 0, md: 60}} p="4">
                {children} {/* Content */}
            </Box>
        </Box>
    )
}

export default SidebarWithHeader

```

### step 347

refer:
https://axios-http.com/docs/intro

```bash
npm install axios
```

create a directory under source with the name services
create a new file under the new directory client.js

```js
import axios from 'axios';

export const getCustomers = async () => {
    try {
        return await axios.get("http://localhost:8080/api/v1/customers")
    } catch (e) {
        throw e;
    }
}
```

### step 348

create a file name .env under react - this is the project folder
install a plugin the ide suggests

```env
VITE_API_BASE_URL=http://localhost:8080
```

update client.js

```js
import axios from 'axios';

export const getCustomers = async () => {
    try {
        return await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`)
    } catch (e) {
        throw e;
    }
}
```

### step 349

update Customer.jsx

```jsx
import {Button} from '@chakra-ui/react';
import SidebarWithHeader from "./shared/SideBar";
import {useEffect} from "react";
import {getCustomers} from "./services/client.js";

const App = () => {
    useEffect(() => {
        getCustomers()
            .then((res) => {
                console.log(res);
            })
            .catch((err) => {
                console.log(err);
            });
    }, []);

    return (
        <SidebarWithHeader>
            <Button colorScheme='teal' variant='outline'>Click me</Button>
        </SidebarWithHeader>
    );
}

export default App;

```

you get cors error in console

### step 350

refer:  https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS

### step 351

create a package in backend folder
craete a file WebMvcConfig.java

```java
package com.kedarnath.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("#{'${cors.allowed-origins}'.split(',')}")
    private List<String> allowedOrigins;
    @Value("#{'${cors.allowed-methods}'.split(',')}")
    private List<String> allowedMethods;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        CorsRegistration corsRegistration = registry.addMapping("/api/**");
        allowedOrigins.forEach(corsRegistration::allowedOrigins);
        allowedMethods.forEach(corsRegistration::allowedMethods);
    }
}

```

update the application.yml

```yaml
server:
  port: 8080
  error:
    include-message: always

cors:
  allowed-origins: "*"
  allowed-methods: "*"

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/customer
    username: kedarnath
    password: password
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
    show_sql: true
  main:
    web-application-type: servlet

```

restart the check the console in frontend. you will get data

### step 352

update app.jsx:

```jsx
import {
    Button, Spinner, Text, Table,
    Thead,
    Tbody,
    Tfoot,
    Tr,
    Th,
    Td,
    TableCaption,
    TableContainer
} from '@chakra-ui/react';
import SidebarWithHeader from "./shared/SideBar";
import {useEffect, useState} from "react";
import {getCustomers} from "./services/client.js";

const App = () => {

    const [customers, setCustomers] = useState([]);
    const [loading, setLoading] = useState(false);


    useEffect(() => {
        setLoading(true);
        setTimeout(() => {
            getCustomers()
                .then((res) => {
                    console.log(res);
                    setCustomers(res.data);
                })
                .catch((err) => {
                    console.log(err);
                }).finally(() => {
                setLoading(false);
            });
        }, 300);
    }, []);

    if (loading) {
        return (<SidebarWithHeader>
                <Spinner
                    thickness='4px'
                    speed='0.65s'
                    emptyColor='gray.200'
                    color='blue.500'
                    size='xl'
                />
            </SidebarWithHeader>
        );
    }

    if (customers.length <= 0) {
        return (
            <SidebarWithHeader>
                <Text>
                    No customers available.
                </Text>
            </SidebarWithHeader>
        )
    }
    return (
        <SidebarWithHeader>
            {customers.map((customer, index) => (
                <p key={index}>{customer.name}</p>
            ))}
        </SidebarWithHeader>
    );
}

export default App;

```

### step 353

refer: https://chakra-templates.vercel.app/components/cards

create a "compoenents" named new directory under src in frontend
move shared folder to this directory
create a file CustomerCard.jsx

```jsx
import {
    Heading,
    Avatar,
    Box,
    Center,
    Image,
    Flex,
    Text,
    Stack,
    Button,
    useColorModeValue,
} from '@chakra-ui/react'

export default function CardWithImage() {
    return (
        <Center py={6}>
            <Box
                maxW={'270px'}
                w={'full'}
                bg={useColorModeValue('white', 'gray.800')}
                boxShadow={'2xl'}
                rounded={'md'}
                overflow={'hidden'}>
                <Image
                    h={'120px'}
                    w={'full'}
                    src={
                        'https://images.unsplash.com/photo-1612865547334-09cb8cb455da?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80'
                    }
                    objectFit="cover"
                    alt="#"
                />
                <Flex justify={'center'} mt={-12}>
                    <Avatar
                        size={'xl'}
                        src={
                            'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?ixlib=rb-1.2.1&q=80&fm=jpg&crop=faces&fit=crop&h=200&w=200&ixid=eyJhcHBfaWQiOjE3Nzg0fQ'
                        }
                        css={{
                            border: '2px solid white',
                        }}
                    />
                </Flex>

                <Box p={6}>
                    <Stack spacing={0} align={'center'} mb={5}>
                        <Heading fontSize={'2xl'} fontWeight={500} fontFamily={'body'}>
                            John Doe
                        </Heading>
                        <Text color={'gray.500'}>Frontend Developer</Text>
                    </Stack>

                    <Stack direction={'row'} justify={'center'} spacing={6}>
                        <Stack spacing={0} align={'center'}>
                            <Text fontWeight={600}>23k</Text>
                            <Text fontSize={'sm'} color={'gray.500'}>
                                Followers
                            </Text>
                        </Stack>
                        <Stack spacing={0} align={'center'}>
                            <Text fontWeight={600}>23k</Text>
                            <Text fontSize={'sm'} color={'gray.500'}>
                                Followers
                            </Text>
                        </Stack>
                    </Stack>

                    <Button
                        w={'full'}
                        mt={8}
                        bg={useColorModeValue('#151f21', 'gray.900')}
                        color={'white'}
                        rounded={'md'}
                        _hover={{
                            transform: 'translateY(-2px)',
                            boxShadow: 'lg',
                        }}>
                        Follow
                    </Button>
                </Box>
            </Box>
        </Center>
    )
}
```

update Customer.jsx:

```jsx
import {
    Button, Spinner, Text, Table,
    Thead,
    Tbody,
    Tfoot,
    Tr,
    Th,
    Td,
    TableCaption,
    TableContainer
} from '@chakra-ui/react';
import SidebarWithHeader from "./components/shared/SideBar";
import {useEffect, useState} from "react";
import {getCustomers} from "./services/client.js";
import CardWithImage from "./components/Card";

const App = () => {

    const [customers, setCustomers] = useState([]);
    const [loading, setLoading] = useState(false);


    useEffect(() => {
        setLoading(true);
        setTimeout(() => {
            getCustomers()
                .then((res) => {
                    console.log(res);
                    setCustomers(res.data);
                })
                .catch((err) => {
                    console.log(err);
                }).finally(() => {
                setLoading(false);
            });
        }, 300);
    }, []);

    if (loading) {
        return (<SidebarWithHeader>
                <Spinner
                    thickness='4px'
                    speed='0.65s'
                    emptyColor='gray.200'
                    color='blue.500'
                    size='xl'
                />
            </SidebarWithHeader>
        );
    }

    if (customers.length <= 0) {
        return (
            <SidebarWithHeader>
                <Text>
                    No customers available.
                </Text>
            </SidebarWithHeader>
        )
    }
    return (
        <SidebarWithHeader>
            {customers.map((customer, index) => (
                <CardWithImage></CardWithImage>
            ))}
        </SidebarWithHeader>
    );
}

export default App;

```

### step 354

refer: https://v2.chakra-ui.com/docs/components/wrap/usage

update Customer.jsx:

```jsx
import {
    Wrap,
    WrapItem,
    Spinner,
    Text,
    Table,
    Thead,
    Tbody,
    Tfoot,
    Tr,
    Th,
    Td,
    TableCaption,
    TableContainer
} from '@chakra-ui/react';
import SidebarWithHeader from "./components/shared/SideBar";
import {useEffect, useState} from "react";
import {getCustomers} from "./services/client.js";
import CardWithImage from "./components/Card";

const App = () => {

    const [customers, setCustomers] = useState([]);
    const [loading, setLoading] = useState(false);


    useEffect(() => {
        setLoading(true);
        setTimeout(() => {
            getCustomers()
                .then((res) => {
                    console.log(res);
                    setCustomers(res.data);
                })
                .catch((err) => {
                    console.log(err);
                }).finally(() => {
                setLoading(false);
            });
        }, 300);
    }, []);

    if (loading) {
        return (<SidebarWithHeader>
                <Spinner
                    thickness='4px'
                    speed='0.65s'
                    emptyColor='gray.200'
                    color='blue.500'
                    size='xl'
                />
            </SidebarWithHeader>
        );
    }

    if (customers.length <= 0) {
        return (
            <SidebarWithHeader>
                <Text>
                    No customers available.
                </Text>
            </SidebarWithHeader>
        )
    }
    return (
        <SidebarWithHeader>
            <Wrap justify='center' spacing='30px'>
                {customers.map((customer, index) => (
                    <WrapItem>
                        <CardWithImage></CardWithImage>
                    </WrapItem>
                ))}
            </Wrap>
        </SidebarWithHeader>
    );
}

export default App;

```

### step 355

update Customer.jsx:

```jsx
import {
    Wrap,
    WrapItem,
    Spinner,
    Text,
    Table,
    Thead,
    Tbody,
    Tfoot,
    Tr,
    Th,
    Td,
    TableCaption,
    TableContainer
} from '@chakra-ui/react';
import SidebarWithHeader from "./components/shared/SideBar";
import {useEffect, useState} from "react";
import {getCustomers} from "./services/client.js";
import CardWithImage from "./components/Card";

const App = () => {

    const [customers, setCustomers] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        setLoading(true);
        setTimeout(() => {
            getCustomers()
                .then((res) => {
                    console.log(res);
                    setCustomers(res.data);
                })
                .catch((err) => {
                    console.log(err);
                }).finally(() => {
                setLoading(false);
            });
        }, 300);
    }, []);

    if (loading) {
        return (
            <SidebarWithHeader>
                <Spinner
                    thickness='4px'
                    speed='0.65s'
                    emptyColor='gray.200'
                    color='blue.500'
                    size='xl'
                />
            </SidebarWithHeader>
        );
    }

    if (customers.length <= 0) {
        return (
            <SidebarWithHeader>
                <Text>
                    No customers available.
                </Text>
            </SidebarWithHeader>
        );
    }

    return (
        <SidebarWithHeader>
            <Wrap justify='center' spacing='30px'>
                {customers.map((customer, index) => (
                    <WrapItem key={customer.id}> {/* Added key prop */}
                        <CardWithImage {...customer}/>
                    </WrapItem>
                ))}
            </Wrap>
        </SidebarWithHeader>
    );
}

export default App;

```

update card.jsx

```jsx
import {
    Heading,
    Avatar,
    Box,
    Center,
    Image,
    Flex,
    Text,
    Stack,
    Button,
    useColorModeValue, Tag,
} from '@chakra-ui/react'

export default function CardWithImage({id, email, image, age, name}) { // Destructure props correctly
    return (
        <Center py={6}>
            <Box
                maxW={'300px'}
                w={'full'}
                bg={useColorModeValue('white', 'gray.800')}
                boxShadow={'2xl'}
                rounded={'md'}
                overflow={'hidden'}>
                <Image
                    h={'120px'}
                    w={'full'}
                    src={
                        image || 'https://images.unsplash.com/photo-1612865547334-09cb8cb455da?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80' // Use the image prop or default
                    }
                    objectFit="cover"
                    alt="Card Image"
                />
                <Flex justify={'center'} mt={-12}>
                    <Avatar
                        size={'xl'}
                        src={
                            'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?ixlib=rb-1.2.1&q=80&fm=jpg&crop=faces&fit=crop&h=200&w=200&ixid=eyJhcHBfaWQiOjE3Nzg0fQ'
                        }
                        css={{
                            border: '2px solid white',
                        }}
                    />
                </Flex>

                <Box p={6}>
                    <Stack spacing={2} align={'center'} mb={5}>
                        <Tag borderRadius='full'>{id}</Tag>
                        <Heading fontSize={'2xl'} fontWeight={500} fontFamily={'body'}>
                            {name || 'Anonymous'} {/* Add name or fallback to 'Anonymous' */}
                        </Heading>
                        <Text color={'gray.500'}>{email}</Text>
                        <Text color={'gray.500'}>Age: {age}</Text>
                    </Stack>
                </Box>
            </Box>
        </Center>
    );
}

```

### step 356

update backend-cd-yml:

```yaml
name: CD - Deploy Backend

on:
  workflow_dispatch:
  push:
    branches:
      - main
    paths:
      - backend/**

jobs:
  build:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:16.3
        env:
          POSTGRES_USER: kedarnath
          POSTGRES_PASSWORD: password
          POSTGRES_DB: customer
        ports:
          - 5432:5432
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    defaults:
      run:
        working-directory: ./backend

    steps:
      - uses: actions/checkout@v4

      - name: Slack commit message and sha
        run: |
          curl -X POST -H 'Content-type: application/json' \
          --data '{"text":"https://github.com/kedarnathyadav/spring-boot-fullstack/commit/${{ github.sha }} - ${{ github.event.head_commit.message }}"}' \
          ${{secrets.SLACK_WEBHOOK_URL}}

      - name: List Files in Working Directory
        run: ls -la

      - name: Send Slack Message
        run: |
          curl -X POST -H 'Content-type: application/json' \
          --data '{"text":"Deployment started :progress_bar: :spring:"}' \
          ${{ secrets.SLACK_WEBHOOK_URL }}

      - uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
          cache: 'maven'

      - name: Send Slack Message
        run: |
          curl -X POST -H 'Content-type: application/json' \
          --data '{"text":":docker: Image tag:${{ steps.build-number.outputs.BUILD_NUMBER }} pushed to https://hub.docker.com/layers/dkedarnath/kedarnath-api/"}' \
          ${{ secrets.SLACK_WEBHOOK_URL }}

      - name: Login to Docker Hub
        uses: docker/login-action@v2
        with:
          username: ${{ secrets.DOCKERHUB_USERNAME }}
          password: ${{ secrets.DOCKERHUB_ACCESS_TOKEN }}

      - name: Set Build Number
        id: build-number
        run: echo "BUILD_NUMBER=$(date '+%d.%m.%Y.%H.%M.%S')" >> $GITHUB_OUTPUT

      - name: Send Slack Message
        run: |
          curl -X POST -H 'Content-type: application/json' \
          --data '{"text":"Building with Maven :maven:"}' \
          ${{ secrets.SLACK_WEBHOOK_URL }}

      - name: Build Package Push with Maven
        run: mvn -ntp -B verify -D docker.image.tag=${{ steps.build-number.outputs.BUILD_NUMBER }} jib:build

      - name: Update Dockerrun.aws.json api image tag with new build number
        run: |
          echo "Dockerrun.aws.json before updating the tag"
          cat Dockerrun.aws.json
          sed -i -E 's|(dkedarnath/kedarnath-api:)[^"]*|\1'${{ steps.build-number.outputs.BUILD_NUMBER }}'|' Dockerrun.aws.json
          echo "Dockerrun.aws.json after updating the tag"
          cat Dockerrun.aws.json

      - name: Send Slack Message
        run: |
          curl -X POST -H 'Content-type: application/json' \
          --data '{"text":":aws: Starting deployment to Elastic Beanstalk "}' \
          ${{ secrets.SLACK_WEBHOOK_URL }}

      - name: Deploy to EB
        uses: einaregilsson/beanstalk-deploy@v22
        with:
          aws_access_key: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws_secret_key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          application_name: ${{ secrets.EB_APPLICATION_NAME }}
          environment_name: ${{ secrets.EB_ENVIRONMENT_NAME }}
          version_label: ${{ steps.build-number.outputs.BUILD_NUMBER }}
          version_description: ${{ github.sha }}
          region: ${{ secrets.EB_REGION }}
          deployment_package: backend/Dockerrun.aws.json

      - name: Send Slack Message
        run: |
          curl -X POST -H 'Content-type: application/json' \
          --data '{"text":":githubloading: Committing to repo  "}' \
          ${{ secrets.SLACK_WEBHOOK_URL }}

      - name: Commit and Push Dockerrun.aws.json
        run: |
          git config user.name github-actions
          git config user.email github-actions@github.com
          git add .
          git commit -m "Update Dockerrun.aws.json docker image with new tag ${{ steps.build-number.outputs.BUILD_NUMBER }}"
          git push

      - name: Send Slack Message
        run: |
          curl -X POST -H 'Content-type: application/json' \
          --data '{"text":"Deployment and commit completed :github_check_mark: :party_blob: - https://kedarnath-api-env.eba-9pwqzaur.us-east-1.elasticbeanstalk.com/"}' \
          ${{ secrets.SLACK_WEBHOOK_URL }}

      - name: Send Slack Message
        if: always()
        run: |
          curl -X POST -H 'Content-type: application/json' \
          --data '{"text":"Job status ${{ job.status }} "} ' \
          ${{ secrets.SLACK_WEBHOOK_URL }}

```

### step 357

push the code to GitHub.

### step 358

do the exercise

### step 359

add gender at backend and update the frontend accordingly

### step 360

create a new fle under the db migration
V3__Add_Gender_Enum_To_Customer.sql:

```txt
ALTER TABLE customer
    ADD COLUMN gender TEXT NOT NULL;

```

Delete all the rows from customer table.

### step 361

UPDATE THE CUSTOMER.JAVA

```java
package com.kedarnath.customer;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(
        name = "customer",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "customer_email_unique",
                        columnNames = "email"
                )
        }
)
public class Customer {
    @Id
    @SequenceGenerator(
            name = "customer_id_seq",
            sequenceName = "customer_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "customer_id_seq"
    )
    private Long id;
    @Column(
            nullable = false
    )
    private String name;
    @Column(
            nullable = false
    )
    private String email;
    @Column(
            nullable = false
    )
    private Integer age;

    @Column(
            nullable = false
    )
    @Enumerated(EnumType.STRING)
    private Gender gender;

    public Customer(Long id, String name, String email, Integer age, Gender gender) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.gender = gender;
    }

    public Customer(String name, String email, Integer age, Gender gender) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.gender = gender;
    }

    public Customer() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id) && Objects.equals(name, customer.name) && Objects.equals(email, customer.email) && Objects.equals(age, customer.age) && gender == customer.gender;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, age, gender);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", gender=" + gender +
                '}';
    }
}

```

While changing customer constructor use refractor so that it will be change in tests as well.

if you run the test cases of only customer a few tests will fail of CustomerJDBCDataAccessServiceTest.java

we will fix it in next steps

### step 362

UPDATE CustomerJDBCDataAccessServiceTest:

```text
package com.kedarnath.customer;

import com.kedarnath.AbstractTestcontainersUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainersUnitTest {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        //Given
        Customer customer = new Customer(
                Faker.name().fullName(),
                Faker.internet().safeEmailAddress() + "" + UUID.randomUUID(),
                20,

                Gender.MALE);
        underTest.insertCustomer(customer);
        //When
        List<Customer> customers = underTest.selectAllCustomers();
        //Then
        assertThat(customers).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        //Given
        String email = Faker.internet().safeEmailAddress() + "" + UUID.randomUUID();
        Customer customer = new Customer(
                Faker.name().fullName(),
                email,
                20,

                Gender.MALE);
        underTest.insertCustomer(customer);

        int id = Math.toIntExact(underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow());

        //When
        Optional<Customer> actual = underTest.selectCustomerById(id);
        //Then
        assertThat(actual).isPresent().hasValueSatisfying(
                c -> {
                    assertThat(c.getId()).isEqualTo(id);
                    assertThat(c.getName()).isEqualTo(customer.getName());
                    assertThat(c.getEmail()).isEqualTo(customer.getEmail());
                    assertThat(c.getAge()).isEqualTo(customer.getAge());

                }
        );
    }


    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        //Given

        int id = -1;
        //When
        var actual = underTest.selectCustomerById(id);
        //Then
        assertThat(actual).isEmpty();
    }

    @Test
    void insertCustomer() {
        //Given

        //When

        //Then
    }

    @Test
    void existsPersonWithEmail() {
        //Given
        String email = Faker.internet().safeEmailAddress() + "" + UUID.randomUUID();
        Customer customer = new Customer(
                Faker.name().fullName(),
                email,
                20,

                Gender.MALE);
        underTest.insertCustomer(customer);
        //When
        boolean actual = underTest.existsPersonWithEmail(email);
        //Then
        assertThat(actual).isTrue();

    }

    @Test
    void existsPersonWithEmailReturnsFalseWhenDoesNotExists() {
        //Given
        String email = Faker.internet().safeEmailAddress() + "" + UUID.randomUUID();


        //When
        boolean actual = underTest.existsPersonWithEmail(email);

        //Then
        assertThat(actual).isFalse();
    }

    @Test
    void existsPersonWithId() {
        //Given
        String email = Faker.internet().safeEmailAddress() + "" + UUID.randomUUID();
        Customer customer = new Customer(
                Faker.name().fullName(),
                email,
                20,

                Gender.MALE);
        underTest.insertCustomer(customer);

        int id = Math.toIntExact(underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow());
        //When
        boolean actual = underTest.existsPersonWithId(id);
        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsPersonWithIdReturnsFalseWhenDoesNotExists() {
        //Given
        int id = -1;
        //When
        boolean actual = underTest.existsPersonWithId(id);
        //Then
        assertThat(actual).isFalse();
    }

    @Test
    void deleteCustomerById() {
        //Given
        String email = Faker.internet().safeEmailAddress() + "" + UUID.randomUUID();
        Customer customer = new Customer(
                Faker.name().fullName(),
                email,
                20,

                Gender.MALE);
        underTest.insertCustomer(customer);

        int id = Math.toIntExact(underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow());

        //When
        underTest.deleteCustomerById(id);
        //Then
        var actual = underTest.selectCustomerById(id);
        assertThat(actual).isNotPresent();
    }

    @Test
    void updateCustomerName() {
        //Given
        String email = Faker.internet().safeEmailAddress() + "" + UUID.randomUUID();
        Customer customer = new Customer(
                Faker.name().fullName(),
                email,
                20,

                Gender.MALE);
        underTest.insertCustomer(customer);
        int id = Math.toIntExact(underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow());

        var newName = "foo";

        //When
        Customer update = new Customer();
        update.setId((long) id);
        update.setName(newName);

        underTest.updateCustomer(update);
        //Then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(newName);
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());

        });
    }

    @Test
    void updateCustomerAge() {
        //Given
        String email = Faker.internet().safeEmailAddress() + "" + UUID.randomUUID();
        Customer customer = new Customer(
                Faker.name().fullName(),
                email,
                20,

                Gender.MALE);
        underTest.insertCustomer(customer);
        int id = Math.toIntExact(underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow());

        var newAge = 30;

        //When
        Customer update = new Customer();
        update.setId((long) id);
        update.setAge(newAge);

        underTest.updateCustomer(update);
        //Then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(newAge);

        });
    }

    @Test
    void updateCustomerEmail() {
        //Given
        String email = Faker.internet().safeEmailAddress() + "" + UUID.randomUUID();
        Customer customer = new Customer(
                Faker.name().fullName(),
                email,
                20,

                Gender.MALE);
        underTest.insertCustomer(customer);
        int id = Math.toIntExact(underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow());

        var newEmail = "foo@gmail.com";

        //When
        Customer update = new Customer();
        update.setId((long) id);
        update.setEmail(newEmail);

        underTest.updateCustomer(update);
        //Then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getAge()).isEqualTo(customer.getAge());

        });
    }

    @Test
    void willUpdateAllPropertiesOfCustomer() {
        //Given
        String email = Faker.internet().safeEmailAddress() + "" + UUID.randomUUID();
        Customer customer = new Customer(
                Faker.name().fullName(),
                email,
                20,
                Gender.MALE);

        underTest.insertCustomer(customer);

        int id = Math.toIntExact(underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow());


        //When update with new name, age and email
        Customer update = new Customer();
        update.setId((long) id);
        update.setName("foo");
        String newEmail = UUID.randomUUID().toString();
        update.setEmail(newEmail);
        update.setAge(30);

        underTest.updateCustomer(update);
        //Then
        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(updated -> {
            assertThat(updated.getId()).isEqualTo(id);
            assertThat(updated.getGender()).isEqualTo(Gender.MALE);
            assertThat(updated.getName()).isEqualTo("foo");
            assertThat(updated.getEmail()).isEqualTo(newEmail);
            assertThat(updated.getAge()).isEqualTo(30);

        });

    }

    @Test
    void willNotUpdateWhenNothingToUpdate() {
        //Given
        String email = Faker.internet().safeEmailAddress() + "" + UUID.randomUUID();
        Customer customer = new Customer(
                Faker.name().fullName(),
                email,
                20,

                Gender.MALE);
        underTest.insertCustomer(customer);
        int id = Math.toIntExact(underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow());

        //When
        Customer update = new Customer();
        update.setId((long) id);

        underTest.updateCustomer(update);

        //Then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());

        });

    }

}
```

UPDATE CustomerRowMapperTest:

```java
package com.kedarnath.customer;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerRowMapperTest {

    @Test
    void mapRow() throws SQLException {
        //Given
        CustomerRowMapper customerRowMapper = new CustomerRowMapper();

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getInt("age")).thenReturn(19);
        when(resultSet.getString("name")).thenReturn("Vikram");
        when(resultSet.getString("email")).thenReturn("Vikram@gmail.com");
        when(resultSet.getString("gender")).thenReturn("FEMALE");
        //When
        Customer actual = customerRowMapper.mapRow(resultSet, 1);
        //Then
        Customer expected = new Customer(1L, "Vikram", "Vikram@gmail.com", 19, Gender.FEMALE);
        assertThat(actual).isEqualTo(expected);
    }


}
```

Update CustomerJDBCDataAccessService:

```text
package com.kedarnath.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDao {

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {

        var sql = """
                SELECT id, name, email, age, gender
                FROM customer
                """;
        List<Customer> customers = jdbcTemplate.query(sql, customerRowMapper);
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        var sql = """
                SELECT id, name, email, age, gender
                FROM customer
                WHERE id = ?
                """;
        return jdbcTemplate.query(sql, customerRowMapper, id).stream().findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        var sql = """
                  INSERT INTO customer(name, email, age, gender)
                  VALUES(?, ?, ?, ?)
                """;
        int result = jdbcTemplate.update(
                sql,
                customer.getName(),
                customer.getEmail(),
                customer.getAge(),
                customer.getGender().name()
        );

        System.out.println("jdbcTemplate.update = " + result);
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        var sql = """
                SELECT count(id)
                FROM customer
                WHERE email = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public boolean existsPersonWithId(Integer id) {
        var sql = """
                SELECT count(id)
                FROM customer
                WHERE id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public void deleteCustomerById(Integer customerId) {
        var sql = """
                DELETE FROM customer
                WHERE id = ?
                """;
        int result = jdbcTemplate.update(sql, customerId);
        System.out.println("deleteCustomerById Result = " + result);
    }

    @Override
    public void updateCustomer(Customer update) {
        if (update.getName() != null) {
            String sql = "UPDATE customer SET name = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    update.getName(),
                    update.getId()
            );
            System.out.println(" Update Customer Name Result = " + result);
        }
        if (update.getAge() != null) {
            String sql = "UPDATE customer SET age = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    update.getAge(),
                    update.getId()
            );
            System.out.println(" Update Customer Age Result = " + result);
        }
        if (update.getEmail() != null) {
            String sql = "UPDATE customer SET email = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    update.getEmail(),
                    update.getId()
            );
            System.out.println(" Update Customer Email Result = " + result);
        }

    }
}

```

update CustomerRowMapper.java

```text
package com.kedarnath.customer;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CustomerRowMapper implements RowMapper<Customer> {

    @Override
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Customer customer = new Customer(
                (long) rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getInt("age"),
                Gender.valueOf(rs.getString("gender")));
        return customer;
    }
}

```

Run all the tests and check it

### step 363:

update CustomerRegistrationRequest.java:

```java
package com.kedarnath.customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age,
        Gender gender
) {

}

```

update CustomerService.java

```java
package com.kedarnath.customer;

import com.kedarnath.exception.DuplicateResourceException;
import com.kedarnath.exception.RequestValidationException;
import com.kedarnath.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomer(Integer id) {
        return customerDao
                .selectCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("customer with id [%s] not found"
                        .formatted(id)
                ));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        String email = customerRegistrationRequest.email();
        if (customerDao.existsPersonWithEmail(email)) {
            throw new DuplicateResourceException("customer with email [%s] already exists"
                    .formatted(email));

        }
        //add
        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age(),
                customerRegistrationRequest.gender());

        customerDao.insertCustomer(customer);
    }

    public void deleteCustomerById(Integer id) {

        if (!customerDao.existsPersonWithId(id)) {
            throw new ResourceNotFoundException("customer with id [%s] not found"
                    .formatted(id));

        }

        customerDao.deleteCustomerById(id);
    }

    public void updateCustomer(Integer customerId, CustomerUpdateRequest updatedRequest) {
        Customer customer = getCustomer(customerId);

        boolean changes = false;

        if (updatedRequest.name() != null && !updatedRequest.name().equals(customer.getName())) {
            customer.setName(updatedRequest.name());
            changes = true;
        }

        if (updatedRequest.age() != null && !updatedRequest.age().equals(customer.getAge())) {
            customer.setAge(updatedRequest.age());
            changes = true;
        }

        if (updatedRequest.email() != null && !updatedRequest.email().equals(customer.getEmail())) {
            if (customerDao.existsPersonWithEmail(updatedRequest.email())) {
                throw new DuplicateResourceException("customer with email [%s] already exists"
                        .formatted(updatedRequest.email()));

            }

            customer.setEmail(updatedRequest.email());
            changes = true;
        }

        if (!changes) {
            throw new RequestValidationException("no data changes found");
        }

        customerDao.updateCustomer(customer);


    }
}

```

update CustomerIntegrationTest.java:

```java
package com.kedarnath.journey;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.kedarnath.customer.Customer;
import com.kedarnath.customer.CustomerRegistrationRequest;
import com.kedarnath.customer.CustomerUpdateRequest;
import com.kedarnath.customer.Gender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIntegrationTest {
    public static final String CUSTOMER_URI = "/api/v1/customers";
    @Autowired
    private WebTestClient webTestClient;
    private static final Random RANDOM = new Random();

    @Test
    void canRegisterACustomer() {
        //create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@kedarnath.com";
        int age = RANDOM.nextInt(1, 100);

        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age, gender
        );
        //send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        //get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        //make sure that customer is present
        Customer expectedCustomer = new Customer(
                name, email, age,
                Gender.MALE);
        assertThat(allCustomers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);


        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();


        expectedCustomer.setId(id);
        //get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .isEqualTo(expectedCustomer);

    }

    @Test
    void canDeleteACustomer() {
        //create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@kedarnath.com";
        int age = RANDOM.nextInt(1, 100);
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age, gender
        );
        //send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        //get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //delete the customer
        webTestClient.delete()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        //get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateACustomer() {
        //create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@kedarnath.com";
        int age = RANDOM.nextInt(1, 100);
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age, gender
        );
        //send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        //get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        String newName = "king";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                newName, null, null
        );
        //update customer
        webTestClient.put()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //get customer by id
        Customer updatedCustomer = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        Customer expected = new Customer(
                id, newName, email, age,
                Gender.MALE);

        assertThat(updatedCustomer).isEqualTo(expected);
    }


}

```

update CustomerIntegrationTest:

```java
package com.kedarnath.journey;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.kedarnath.customer.Customer;
import com.kedarnath.customer.CustomerRegistrationRequest;
import com.kedarnath.customer.CustomerUpdateRequest;
import com.kedarnath.customer.Gender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIntegrationTest {
    public static final String CUSTOMER_URI = "/api/v1/customers";
    @Autowired
    private WebTestClient webTestClient;
    private static final Random RANDOM = new Random();

    @Test
    void canRegisterACustomer() {
        //create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@kedarnath.com";
        int age = RANDOM.nextInt(1, 100);

        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age, gender
        );
        //send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        //get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        //make sure that customer is present
        Customer expectedCustomer = new Customer(
                name, email, age,
                gender);
        assertThat(allCustomers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);


        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();


        expectedCustomer.setId(id);
        //get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .isEqualTo(expectedCustomer);

    }

    @Test
    void canDeleteACustomer() {
        //create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@kedarnath.com";
        int age = RANDOM.nextInt(1, 100);
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age, gender
        );
        //send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        //get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //delete the customer
        webTestClient.delete()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        //get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateACustomer() {
        //create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@kedarnath.com";
        int age = RANDOM.nextInt(1, 100);
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age, gender
        );
        //send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        //get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        String newName = "king";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                newName, null, null
        );
        //update customer
        webTestClient.put()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //get customer by id
        Customer updatedCustomer = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        Customer expected = new Customer(
                id, newName, email, age,
                gender);

        assertThat(updatedCustomer).isEqualTo(expected);
    }


}

```

update CustomerServiceTest:

```java
package com.kedarnath.customer;

import com.kedarnath.exception.DuplicateResourceException;
import com.kedarnath.exception.RequestValidationException;
import com.kedarnath.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService underTest;

    @Mock
    private CustomerDao customerDao;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao);
    }


    @Test
    void getAllCustomers() {
        //When
        underTest.getAllCustomers();
        //Then
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        //Given
        int id = 10;
        Customer customer = new Customer(
                (long) id, "alex", "alex@gmail.com", 19,
                Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        //When
        Customer actual = underTest.getCustomer(id);
        //Then
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowWhenGetCustomerReturnEmptyOptional() {
        //Given
        int id = 10;
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());
        //When
        //Then
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "customer with id [%s] not found".formatted(id));

    }

    @Test
    void addCustomer() {
        //Given
        String email = "alex@gmail.com";

        when(customerDao.existsPersonWithEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "alex", email, 19, Gender.MALE
        );
        //When
        underTest.addCustomer(request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void addCustomerThrowsExceptionWhenEmailExists() {
        // Given
        String email = "alex@gmail.com";
        when(customerDao.existsPersonWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "alex", email, 19, Gender.MALE
        );

        // When & Then
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("customer with email [alex@gmail.com] already exists");

        // Verify that insertCustomer is never called
        verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void deleteCustomerById() {
        //Given
        int id = 10;

        when(customerDao.existsPersonWithId(id)).thenReturn(true);
        //When
        underTest.deleteCustomerById(id);
        //Then
        verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void deleteCustomerByIdThrowsExceptionWhenIdNotExists() {
        //Given
        int id = 10;

        when(customerDao.existsPersonWithId(id)).thenReturn(false);
        //When
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found"
                        .formatted(id));
        //Then
        verify(customerDao, never()).deleteCustomerById(any());
    }

    @Test
    void canUpdateAllCustomerproperties() {
        //Given
        int id = 10;
        String email = "alex@gmail.com";
        Customer customer = new Customer(
                (long) id, "alex", email, 19,
                Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        String newEmail = "alexandra@gmail.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "alexandra", newEmail, 19
        );

        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);

        //When
        underTest.updateCustomer(id, updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
    }

    @Test
    void canUpdateOnlyCustomerName() {
        //Given
        int id = 10;
        Customer customer = new Customer(
                (long) id, "alex", "alex@gmail.com", 19,
                Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "alexandra", null, null
        );


        //When
        underTest.updateCustomer(id, updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void canUpdateOnlyCustomerAge() {
        //Given
        int id = 10;
        Customer customer = new Customer(
                (long) id, "alex", "alex@gmail.com", 19,
                Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, null, 26
        );


        //When
        underTest.updateCustomer(id, updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
    }

    @Test
    void canUpdateOnlyCustomerEmail() {
        //Given
        int id = 10;
        Customer customer = new Customer(
                (long) id, "alex", "alex@gmail.com", 19,
                Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        String newEmail = "alexandra@gmail.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, newEmail, null
        );
        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);
        //When
        underTest.updateCustomer(id, updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail);
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void willThrowWhenTryingToUpdateEmailWhenAlreadyTaken() {
        //Given
        int id = 10;
        Customer customer = new Customer(
                (long) id, "alex", "alex@gmail.com", 19,
                Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        String newEmail = "alexandra@gmail.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, newEmail, null
        );
        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(true);
        //When
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("customer with email [%s] already exists"
                        .formatted(newEmail));

        //Then
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void willThrowWhenNoChnageInUpdate() {
        //Given
        int id = 10;
        String email = "alex@gmail.com";
        Customer customer = new Customer(
                (long) id, "alex", email, 19,
                Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                customer.getName(), customer.getEmail(), customer.getAge()
        );

        //When
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("no data changes found");

        //Then
        verify(customerDao, never()).updateCustomer(any());
    }

}
```

### step 364:

update card.jsx:

```jsx
import {
    Heading,
    Avatar,
    Box,
    Center,
    Image,
    Flex,
    Text,
    Stack,
    useColorModeValue, Tag,
} from '@chakra-ui/react'

export default function CardWithImage({id, email, image, age, name, gender, imageNumber}) { // Destructure props correctly
    const randomUserGender = gender === "MALE" ? "men" : "women";
    return (
        <Center py={6}>
            <Box
                maxW={'300px'}
                w={'full'}
                bg={useColorModeValue('white', 'gray.800')}
                boxShadow={'2xl'}
                rounded={'md'}
                overflow={'hidden'}>
                <Image
                    h={'120px'}
                    w={'full'}
                    src={
                        image || 'https://images.unsplash.com/photo-1612865547334-09cb8cb455da?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80' // Use the image prop or default
                    }
                    objectFit="cover"
                    alt="Card Image"
                />
                <Flex justify={'center'} mt={-12}>
                    <Avatar
                        size={'xl'}
                        src={
                            `https://randomuser.me/api/portraits/${randomUserGender}/${imageNumber}.jpg`
                        }
                        css={{
                            border: '2px solid white',
                        }}
                    />
                </Flex>


                <Box p={6}>
                    <Stack spacing={2} align={'center'} mb={5}>
                        <Tag borderRadius='full'>{id}</Tag>
                        <Heading fontSize={'2xl'} fontWeight={500} fontFamily={'body'}>
                            {name || 'Anonymous'} {/* Add name or fallback to 'Anonymous' */}
                        </Heading>
                        <Text color={'gray.500'}>{email}</Text>
                        <Text color={'gray.500'}>Age: {age}</Text>
                    </Stack>
                </Box>
            </Box>
        </Center>
    );
}

```

update Customer.jsx:

```jsx
import {
    Wrap,
    WrapItem,
    Spinner,
    Text,
    Table,
    Thead,
    Tbody,
    Tfoot,
    Tr,
    Th,
    Td,
    TableCaption,
    TableContainer
} from '@chakra-ui/react';
import SidebarWithHeader from "./components/shared/SideBar";
import {useEffect, useState} from "react";
import {getCustomers} from "./services/client.js";
import CardWithImage from "./components/Card";

const App = () => {

    const [customers, setCustomers] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        setLoading(true);
        setTimeout(() => {
            getCustomers()
                .then((res) => {
                    console.log(res);
                    setCustomers(res.data);
                })
                .catch((err) => {
                    console.log(err);
                }).finally(() => {
                setLoading(false);
            });
        }, 300);
    }, []);

    if (loading) {
        return (
            <SidebarWithHeader>
                <Spinner
                    thickness='4px'
                    speed='0.65s'
                    emptyColor='gray.200'
                    color='blue.500'
                    size='xl'
                />
            </SidebarWithHeader>
        );
    }

    if (customers.length <= 0) {
        return (
            <SidebarWithHeader>
                <Text>
                    No customers available.
                </Text>
            </SidebarWithHeader>
        );
    }

    return (
        <SidebarWithHeader>
            <Wrap justify='center' spacing='30px'>
                {customers.map((customer, index) => (
                    <WrapItem key={customer.id}> {/* Added key prop */}
                        <CardWithImage {...customer}
                                       imageNumber={index}/>
                    </WrapItem>
                ))}
            </Wrap>
        </SidebarWithHeader>
    );
}

export default App;

```

I made a mistake in integration test as its adding the same name everytime:

update CustomerIntegrationTest.java:

```java
package com.kedarnath.journey;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.kedarnath.customer.Customer;
import com.kedarnath.customer.CustomerRegistrationRequest;
import com.kedarnath.customer.CustomerUpdateRequest;
import com.kedarnath.customer.Gender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIntegrationTest {
    public static final String CUSTOMER_URI = "/api/v1/customers";
    @Autowired
    private WebTestClient webTestClient;
    private static final Random RANDOM = new Random();

    @Test
    void canRegisterACustomer() {
        //create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@kedarnath.com";
        int age = RANDOM.nextInt(1, 100);

        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age, gender
        );
        //send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        //get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        //make sure that customer is present
        Customer expectedCustomer = new Customer(
                name, email, age,
                gender);
        assertThat(allCustomers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);


        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();


        expectedCustomer.setId(id);
        //get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .isEqualTo(expectedCustomer);

    }

    @Test
    void canDeleteACustomer() {
        //create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@kedarnath.com";
        int age = RANDOM.nextInt(1, 100);
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age, gender
        );
        //send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        //get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //delete the customer
        webTestClient.delete()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        //get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateACustomer() {
        //create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@kedarnath.com";
        int age = RANDOM.nextInt(1, 100);
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age, gender
        );
        //send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        //get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        faker = new Faker();
        fakerName = faker.name();
        String newName = fakerName.fullName();

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                newName, null, null
        );
        //update customer
        webTestClient.put()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //get customer by id
        Customer updatedCustomer = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        Customer expected = new Customer(
                id, newName, email, age,
                gender);

        assertThat(updatedCustomer).isEqualTo(expected);
    }


}

```

### step 365:

now ssh into EC2 Instance database and delete all the customers as they got no gender if you remember.

push it to GitHub then it will be deployed

### step 366:

download the latest docker image and test it
make sure you use the port 8088 if you are connecting it to frontend in .env file

then use

```bash
docker compose down
```

```bash
docker pull
#it will dowload the latest image
```

```bash
dockercompose up -d
```

That's it

### step 367:

make sure you made a note of the issue you have faced

we got an error of incompatibily so we have updated the docker compose filw with postgres 16 to match

```yaml
services:
  db:
    container_name: postgres
    image: postgres:16
    #    above line we added 16
    environment:
      POSTGRES_USER: kedarnath
      POSTGRES_PASSWORD: password
      PGDATA: /data/postgres
    volumes:
      - db:/data/postgres
    ports:
      - "5432:5432"
    networks:
      - db
    restart: unless-stopped
  amigoscode-api:
    container_name: kedarnath-api
    image: dkedarnath/kedarnath-api
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/customer
    ports:
      - "8088:8080"
    networks:
      - db
    depends_on:
      - db
    restart: unless-stopped

networks:
  db:
    driver: bridge

volumes:
  db:


```

### step 368:

we are going send request from frontend to backend

### step 369:

create a file CreateCustomerDrawer.jsx under components:

```jsx
import {Button} from "@chakra-ui/react";

const AddIcon = () => "+";

const CreateCustomerDrawer = () => {
    return (
        <Button
            leftIcon={<AddIcon/>}
            colorScheme={"teal"}
            onClick={() => alert("create new customer")}
        >
            create customer
        </Button>
    )
}

export default CreateCustomerDrawer;
```

add this in the Customer.jsx

### step 370:

add side drawer in CreateCustomerDrawer.jsx

```jsx
import {
    Button,
    Drawer,
    DrawerBody,
    DrawerCloseButton, DrawerContent, DrawerFooter,
    DrawerHeader,
    DrawerOverlay, Input,
    useDisclosure
} from "@chakra-ui/react";

const AddIcon = () => "+";

const CreateCustomerDrawer = () => {
    const {isOpen, onOpen, onClose} = useDisclosure()
    return (
        <>
            <Button
                leftIcon={<AddIcon/>}
                colorScheme={"teal"}
                onClick={onOpen}
            >
                create customer
            </Button>
            <Drawer isOpen={isOpen} onClose={onClose} size={"xl"}>
                <DrawerOverlay/>
                <DrawerContent>
                    <DrawerCloseButton/>
                    <DrawerHeader>Create your account</DrawerHeader>

                    <DrawerBody>
                        <form
                            id='my-form'
                            onSubmit={(e) => {
                                e.preventDefault()
                                console.log('submitted')
                            }}
                        >
                            <Input name='nickname' placeholder='Type here...'/>
                        </form>
                    </DrawerBody>

                    <DrawerFooter>
                        <Button type='submit' form='my-form'>
                            Save
                        </Button>
                    </DrawerFooter>
                </DrawerContent>
            </Drawer>
        </>
    )
}

export default CreateCustomerDrawer;

```

### step 371:

refer https://formik.org/

```bash
npm i formik yup
```

### step 372:

create file CreateCustomerForm.jsx under components

```jsx
import {Formik, Form, useField} from 'formik';
import * as Yup from 'yup';

const MyTextInput = ({label, ...props}) => {
    // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
    // which we can spread on <input>. We can use field meta to show an error
    // message if the field is invalid and it has been touched (i.e. visited)
    const [field, meta] = useField(props);
    return (
        <>
            <label htmlFor={props.id || props.name}>{label}</label>
            <input className="text-input" {...field} {...props} />
            {meta.touched && meta.error ? (
                <div className="error">{meta.error}</div>
            ) : null}
        </>
    );
};

const MyCheckbox = ({children, ...props}) => {
    // React treats radios and checkbox inputs differently from other input types: select and textarea.
    // Formik does this too! When you specify `type` to useField(), it will
    // return the correct bag of props for you -- a `checked` prop will be included
    // in `field` alongside `name`, `value`, `onChange`, and `onBlur`
    const [field, meta] = useField({...props, type: 'checkbox'});
    return (
        <div>
            <label className="checkbox-input">
                <input type="checkbox" {...field} {...props} />
                {children}
            </label>
            {meta.touched && meta.error ? (
                <div className="error">{meta.error}</div>
            ) : null}
        </div>
    );
};

const MySelect = ({label, ...props}) => {
    const [field, meta] = useField(props);
    return (
        <div>
            <label htmlFor={props.id || props.name}>{label}</label>
            <select {...field} {...props} />
            {meta.touched && meta.error ? (
                <div className="error">{meta.error}</div>
            ) : null}
        </div>
    );
};

// And now we can use these
const CreateCustomerForm = () => {
    return (
        <>
            <h1>Subscribe!</h1>
            <Formik
                initialValues={{
                    firstName: '',
                    lastName: '',
                    email: '',
                    acceptedTerms: false, // added for our checkbox
                    jobType: '', // added for our select
                }}
                validationSchema={Yup.object({
                    firstName: Yup.string()
                        .max(15, 'Must be 15 characters or less')
                        .required('Required'),
                    lastName: Yup.string()
                        .max(20, 'Must be 20 characters or less')
                        .required('Required'),
                    email: Yup.string()
                        .email('Invalid email address')
                        .required('Required'),
                    acceptedTerms: Yup.boolean()
                        .required('Required')
                        .oneOf([true], 'You must accept the terms and conditions.'),
                    jobType: Yup.string()
                        .oneOf(
                            ['designer', 'development', 'product', 'other'],
                            'Invalid Job Type'
                        )
                        .required('Required'),
                })}
                onSubmit={(values, {setSubmitting}) => {
                    setTimeout(() => {
                        alert(JSON.stringify(values, null, 2));
                        setSubmitting(false);
                    }, 400);
                }}
            >
                <Form>
                    <MyTextInput
                        label="First Name"
                        name="firstName"
                        type="text"
                        placeholder="Jane"
                    />

                    <MyTextInput
                        label="Last Name"
                        name="lastName"
                        type="text"
                        placeholder="Doe"
                    />

                    <MyTextInput
                        label="Email Address"
                        name="email"
                        type="email"
                        placeholder="jane@formik.com"
                    />

                    <MySelect label="Job Type" name="jobType">
                        <option value="">Select a job type</option>
                        <option value="designer">Designer</option>
                        <option value="development">Developer</option>
                        <option value="product">Product Manager</option>
                        <option value="other">Other</option>
                    </MySelect>

                    <MyCheckbox name="acceptedTerms">
                        I accept the terms and conditions
                    </MyCheckbox>

                    <button type="submit">Submit</button>
                </Form>
            </Formik>
        </>
    );
};

export default CreateCustomerForm;
```

add the above to CreateCustomerDrawer.jsx

```jsx
import {
    Button,
    Drawer,
    DrawerBody,
    DrawerCloseButton, DrawerContent, DrawerFooter,
    DrawerHeader,
    DrawerOverlay, Input,
    useDisclosure
} from "@chakra-ui/react";
import CreateCustomerForm from "./CreateCustomerForm.jsx";

const AddIcon = () => "+";

const CreateCustomerDrawer = () => {
    const {isOpen, onOpen, onClose} = useDisclosure()
    return (
        <>
            <Button
                leftIcon={<AddIcon/>}
                colorScheme={"teal"}
                onClick={onOpen}
            >
                create customer
            </Button>
            <Drawer isOpen={isOpen} onClose={onClose} size={"xl"}>
                <DrawerOverlay/>
                <DrawerContent>
                    <DrawerCloseButton/>
                    <DrawerHeader>Create your account</DrawerHeader>

                    <DrawerBody>
                        <CreateCustomerForm/>
                    </DrawerBody>

                    <DrawerFooter>
                        <Button type='submit' form='my-form'>
                            Save
                        </Button>
                    </DrawerFooter>
                </DrawerContent>
            </Drawer>
        </>
    )
}

export default CreateCustomerDrawer;

```

### step 373:

update the CreateCustomerForm.jsx

```jsx
import {Formik, Form, useField} from 'formik';
import * as Yup from 'yup';
import {Alert, AlertIcon, Box, Button, FormLabel, Input, Select} from "@chakra-ui/react";

const MyTextInput = ({label, ...props}) => {
    // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
    // which we can spread on <input>. We can use field meta to show an error
    // message if the field is invalid and it has been touched (i.e. visited)
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error">
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

const MySelect = ({label, ...props}) => {
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Select {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error">
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

// And now we can use these
const CreateCustomerForm = () => {
    return (
        <>
            <Formik
                initialValues={{
                    name: '',
                    email: '',
                    age: 0,
                    gender: '', // added for our select
                }}
                validationSchema={Yup.object({
                    name: Yup.string()
                        .max(20, 'Must be 20 characters or less')
                        .required('Required'),
                    email: Yup.string()
                        .email('Invalid email address')
                        .required('Required'),
                    age: Yup.number()
                        .min(16, 'Must be atleast 16 years of age')
                        .max(100, 'Must be less than 100 years of age')
                        .required('Required'),
                    gender: Yup.string()
                        .oneOf(
                            ['MALE', 'FEMALE'],
                            'Invalid gender'
                        )
                        .required('Required'),
                })}
                onSubmit={(values, {setSubmitting}) => {
                    setTimeout(() => {
                        alert(JSON.stringify(values, null, 2));
                        setSubmitting(false);
                    }, 400);
                }}
            >
                <Form>
                    <MyTextInput
                        label="name"
                        name="name"
                        type="text"
                        placeholder="Enter your name"
                    />


                    <MyTextInput
                        label="Email"
                        name="email"
                        type="text"
                        placeholder="Enter your email"
                    />
                    <MyTextInput
                        label="age"
                        name="age"
                        type="number"
                        placeholder="16"
                    />

                    <MySelect label="gender" name="gender">
                        <option value="">Select a gender</option>
                        <option value="MALE">Male</option>
                        <option value="FEMALE">Female</option>
                    </MySelect>

                    <Button type="submit" mt={"2"}>Submit</Button>
                </Form>
            </Formik>
        </>
    );
};

export default CreateCustomerForm;
```

### step 374:

update CreateCustomerDrawer.jsx

```jsx
import {
    Button,
    Drawer,
    DrawerBody,
    DrawerCloseButton, DrawerContent, DrawerFooter,
    DrawerHeader,
    DrawerOverlay, Input,
    useDisclosure
} from "@chakra-ui/react";
import CreateCustomerForm from "./CreateCustomerForm.jsx";

const AddIcon = () => "+";
const CloseIcon = () => "X";

const CreateCustomerDrawer = () => {
    const {isOpen, onOpen, onClose} = useDisclosure()
    return (
        <>
            <Button
                leftIcon={<AddIcon/>}
                colorScheme={"teal"}
                onClick={onOpen}
            >
                create customer
            </Button>
            <Drawer isOpen={isOpen} onClose={onClose} size={"xl"}>
                <DrawerOverlay/>
                <DrawerContent>
                    <DrawerCloseButton/>
                    <DrawerHeader>Create New Customer</DrawerHeader>

                    <DrawerBody>
                        <CreateCustomerForm/>
                    </DrawerBody>

                    <DrawerFooter>
                        <Button
                            leftIcon={<CloseIcon/>}
                            colorScheme={"teal"}
                            onClick={onClose}
                        >
                            Close
                        </Button>
                    </DrawerFooter>
                </DrawerContent>
            </Drawer>
        </>
    )
}

export default CreateCustomerDrawer;

```

update CreateCustomerForm.jsx

```jsx
import {Formik, Form, useField} from 'formik';
import * as Yup from 'yup';
import {Alert, AlertIcon, Box, Button, FormLabel, Input, Select, Stack} from "@chakra-ui/react";

const MyTextInput = ({label, ...props}) => {
    // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
    // which we can spread on <input>. We can use field meta to show an error
    // message if the field is invalid and it has been touched (i.e. visited)
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={"2"}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

const MySelect = ({label, ...props}) => {
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Select {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={"2"}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

// And now we can use these
const CreateCustomerForm = () => {
    return (
        <>
            <Formik
                initialValues={{
                    name: '',
                    email: '',
                    age: 0,
                    gender: '', // added for our select
                }}
                validationSchema={Yup.object({
                    name: Yup.string()
                        .max(20, 'Must be 20 characters or less')
                        .required('Required'),
                    email: Yup.string()
                        .email('Invalid email address')
                        .required('Required'),
                    age: Yup.number()
                        .min(16, 'Must be atleast 16 years of age')
                        .max(100, 'Must be less than 100 years of age')
                        .required('Required'),
                    gender: Yup.string()
                        .oneOf(
                            ['MALE', 'FEMALE'],
                            'Invalid gender'
                        )
                        .required('Required'),
                })}
                onSubmit={(values, {setSubmitting}) => {
                    setTimeout(() => {
                        alert(JSON.stringify(values, null, 2));
                        setSubmitting(false);
                    }, 400);
                }}
            >
                <Form>
                    <Stack spacing={"24px"}>
                        <MyTextInput
                            label="Name"
                            name="name"
                            type="text"
                            placeholder="Enter your name"
                        />


                        <MyTextInput
                            label="Email"
                            name="email"
                            type="text"
                            placeholder="Enter your email"
                        />
                        <MyTextInput
                            label="Age"
                            name="age"
                            type="number"
                            placeholder="16"
                        />

                        <MySelect label="Gender" name="gender">
                            <option value="">Select a gender</option>
                            <option value="MALE">Male</option>
                            <option value="FEMALE">Female</option>
                        </MySelect>

                        <Button type="submit" mt={2}>Submit</Button>
                    </Stack>
                </Form>
            </Formik>
        </>
    );
};

export default CreateCustomerForm;
```

### step 375:

update CreateCustomerForm.jsx
--added feature the submit button will be enabled only when the fields are valid

```jsx
import {Formik, Form, useField} from 'formik';
import * as Yup from 'yup';
import {Alert, AlertIcon, Box, Button, FormLabel, Input, Select, Stack} from "@chakra-ui/react";

const MyTextInput = ({label, ...props}) => {
    // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
    // which we can spread on <input>. We can use field meta to show an error
    // message if the field is invalid, and it has been touched (i.e. visited)
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={"2"}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

const MySelect = ({label, ...props}) => {
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Select {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={"2"}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

// And now we can use these
const CreateCustomerForm = () => {
    return (
        <>
            <Formik
                initialValues={{
                    name: '',
                    email: '',
                    age: 0,
                    gender: '', // added for our select
                }}
                validationSchema={Yup.object({
                    name: Yup.string()
                        .max(20, 'Must be 20 characters or less')
                        .required('Required'),
                    email: Yup.string()
                        .email('Invalid email address')
                        .required('Required'),
                    age: Yup.number()
                        .integer('Age must be a whole number')
                        .min(16, 'Must be at least 16 years of age')
                        .max(100, 'Must be less than 100 years of age')
                        .required('Required'),
                    gender: Yup.string()
                        .oneOf(
                            ['MALE', 'FEMALE'],
                            'Invalid gender'
                        )
                        .required('Required'),
                })}
                onSubmit={(values, {setSubmitting}) => {
                    setTimeout(() => {
                        alert(JSON.stringify(values, null, 2));
                        setSubmitting(false);
                    }, 400);
                }}
            >
                {({isValid, isSubmitting}) => (
                    <Form>
                        <Stack spacing={"24px"}>
                            <MyTextInput
                                label="Name"
                                name="name"
                                type="text"
                                placeholder="Enter your name"
                            />


                            <MyTextInput
                                label="Email"
                                name="email"
                                type="text"
                                placeholder="Enter your email"
                            />
                            <MyTextInput
                                label="Age"
                                name="age"
                                type="number"
                                placeholder="16"
                            />

                            <MySelect label="Gender" name="gender">
                                <option value="">Select a gender</option>
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                            </MySelect>

                            <Button
                                disabled={!isValid || isSubmitting}
                                type="submit" mt={2}>Submit</Button>
                        </Stack>
                    </Form>
                )}
            </Formik>
        </>
    );
};

export default CreateCustomerForm;
```

### step 376:

update client.js:

```js
import axios from 'axios';

export const getCustomers = async () => {
    try {
        return await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`);
    } catch (e) {
        throw e;
    }
}

export const saveCustomer = async (customer) => {
    try {
        return await axios.post(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`,
            customer // <-- move this inside the axios.post() function call
        );
    } catch (e) {
        throw e;
    }
}

```

aded function save customer in client.js services

### step 377:

Update the customercreateForm.jsx:

```jsx
import {Formik, Form, useField} from 'formik';
import * as Yup from 'yup';
import {Alert, AlertIcon, Box, Button, FormLabel, Input, Select, Stack} from "@chakra-ui/react";
import {saveCustomer} from "../services/client.js";

const MyTextInput = ({label, ...props}) => {
    // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
    // which we can spread on <input>. We can use field meta to show an error
    // message if the field is invalid, and it has been touched (i.e. visited)
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={"2"}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

const MySelect = ({label, ...props}) => {
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Select {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={"2"}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

// And now we can use these
const CreateCustomerForm = () => {
    return (
        <>
            <Formik
                initialValues={{
                    name: '',
                    email: '',
                    age: 0,
                    gender: '', // added for our select
                }}
                validationSchema={Yup.object({
                    name: Yup.string()
                        .max(20, 'Must be 20 characters or less')
                        .required('Required'),
                    email: Yup.string()
                        .email('Invalid email address')
                        .required('Required'),
                    age: Yup.number()
                        .integer('Age must be a whole number')
                        .min(16, 'Must be at least 16 years of age')
                        .max(100, 'Must be less than 100 years of age')
                        .required('Required'),
                    gender: Yup.string()
                        .oneOf(
                            ['MALE', 'FEMALE'],
                            'Invalid gender'
                        )
                        .required('Required'),
                })}
                onSubmit={(customer, {setSubmitting}) => {
                    setSubmitting(true);
                    saveCustomer(customer)
                        .then(res => {
                            console.log(res);
                            alert("customer saved");
                        }).catch(err => {
                        console.log(err);
                    }).finally(() => {
                        setSubmitting(false);
                    });
                }}
            >
                {({isValid, isSubmitting}) => (
                    <Form>
                        <Stack spacing={"24px"}>
                            <MyTextInput
                                label="Name"
                                name="name"
                                type="text"
                                placeholder="Enter your name"
                            />


                            <MyTextInput
                                label="Email"
                                name="email"
                                type="text"
                                placeholder="Enter your email"
                            />
                            <MyTextInput
                                label="Age"
                                name="age"
                                type="number"
                                placeholder="16"
                            />

                            <MySelect label="Gender" name="gender">
                                <option value="">Select a gender</option>
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                            </MySelect>

                            <Button
                                disabled={!isValid || isSubmitting}
                                type="submit" mt={2}>Submit</Button>
                        </Stack>
                    </Form>
                )}
            </Formik>
        </>
    );
};

export default CreateCustomerForm;
```

--added the saveCustomer function in the form to send the customer details.
we need to fix like the form should close after submitting and should get notification instead of alert

### step 378:

delete all the data from database because from front end it will start adding
it will start from 1 and it might get issues

```text
delete
from customer;

ALTER SEQUENCE customer_id_seq RESTART WITH 1;
```

to get the page get refreshed automatically after adding the user:

```jsx
import {
    Wrap,
    WrapItem,
    Spinner,
    Text,
    Table,
    Thead,
    Tbody,
    Tfoot,
    Tr,
    Th,
    Td,
    TableCaption,
    TableContainer
} from '@chakra-ui/react';
import SidebarWithHeader from "./components/shared/SideBar";
import {useEffect, useState} from "react";
import {getCustomers} from "./services/client.js";
import CardWithImage from "./components/Card";
import CreateCustomerDrawer from "./components/CreateCustomerDrawer.jsx";

const App = () => {

    const [customers, setCustomers] = useState([]);
    const [loading, setLoading] = useState(false);

    const fetchCustomers = () => {
        setLoading(true);
        getCustomers().then(res => {
            setCustomers(res.data);
        }).catch((err) => {
            console.log(err);
        }).finally(() => {
            setLoading(false);
        });
    }
    useEffect(() => {
        fetchCustomers();
    }, []);

    if (loading) {
        return (
            <SidebarWithHeader>
                <Spinner
                    thickness='4px'
                    speed='0.65s'
                    emptyColor='gray.200'
                    color='blue.500'
                    size='xl'
                />
            </SidebarWithHeader>
        );
    }

    if (customers.length <= 0) {
        return (
            <SidebarWithHeader>
                <CreateCustomerDrawer
                    fetchCustomers={fetchCustomers}
                />
                <Text mt={5}>
                    No customers available.
                </Text>
            </SidebarWithHeader>
        );
    }

    return (
        <SidebarWithHeader>
            <CreateCustomerDrawer
                fetchCustomers={fetchCustomers}
            />
            <Wrap justify='center' spacing='30px'>
                {customers.map((customer, index) => (
                    <WrapItem key={customer.id}> {/* Added key prop */}
                        <CardWithImage {...customer}
                                       imageNumber={index}/>
                    </WrapItem>
                ))}
            </Wrap>
        </SidebarWithHeader>
    );
}

export default App;

```

```jsx
import {
    Button,
    Drawer,
    DrawerBody,
    DrawerCloseButton, DrawerContent, DrawerFooter,
    DrawerHeader,
    DrawerOverlay, Input,
    useDisclosure
} from "@chakra-ui/react";
import CreateCustomerForm from "./CreateCustomerForm.jsx";

const AddIcon = () => "+";
const CloseIcon = () => "X";

const CreateCustomerDrawer = ({fetchCustomers}) => {
    const {isOpen, onOpen, onClose} = useDisclosure()
    return (
        <>
            <Button
                leftIcon={<AddIcon/>}
                colorScheme={"teal"}
                onClick={onOpen}
            >
                create customer
            </Button>
            <Drawer isOpen={isOpen} onClose={onClose} size={"xl"}>
                <DrawerOverlay/>
                <DrawerContent>
                    <DrawerCloseButton/>
                    <DrawerHeader>Create New Customer</DrawerHeader>

                    <DrawerBody>
                        <CreateCustomerForm
                            fetchCustomers={fetchCustomers}
                        />
                    </DrawerBody>

                    <DrawerFooter>
                        <Button
                            leftIcon={<CloseIcon/>}
                            colorScheme={"teal"}
                            onClick={onClose}
                        >
                            Close
                        </Button>
                    </DrawerFooter>
                </DrawerContent>
            </Drawer>
        </>
    )
}

export default CreateCustomerDrawer;

```

```jsx
import {Formik, Form, useField} from 'formik';
import * as Yup from 'yup';
import {Alert, AlertIcon, Box, Button, FormLabel, Input, Select, Stack} from "@chakra-ui/react";
import {saveCustomer} from "../services/client.js";

const MyTextInput = ({label, ...props}) => {
    // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
    // which we can spread on <input>. We can use field meta to show an error
    // message if the field is invalid, and it has been touched (i.e. visited)
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={"2"}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

const MySelect = ({label, ...props}) => {
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Select {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={"2"}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

// And now we can use these
const CreateCustomerForm = ({fetchCustomers}) => {
    return (
        <>
            <Formik
                initialValues={{
                    name: '',
                    email: '',
                    age: 0,
                    gender: '', // added for our select
                }}
                validationSchema={Yup.object({
                    name: Yup.string()
                        .max(20, 'Must be 20 characters or less')
                        .required('Required'),
                    email: Yup.string()
                        .email('Invalid email address')
                        .required('Required'),
                    age: Yup.number()
                        .integer('Age must be a whole number')
                        .min(16, 'Must be at least 16 years of age')
                        .max(100, 'Must be less than 100 years of age')
                        .required('Required'),
                    gender: Yup.string()
                        .oneOf(
                            ['MALE', 'FEMALE'],
                            'Invalid gender'
                        )
                        .required('Required'),
                })}
                onSubmit={(customer, {setSubmitting}) => {
                    setSubmitting(true);
                    saveCustomer(customer)
                        .then(res => {
                            console.log(res);
                            alert("customer saved");
                            fetchCustomers();
                        }).catch(err => {
                        console.log(err);
                    }).finally(() => {
                        setSubmitting(false);
                    });
                }}
            >
                {({isValid, isSubmitting}) => (
                    <Form>
                        <Stack spacing={"24px"}>
                            <MyTextInput
                                label="Name"
                                name="name"
                                type="text"
                                placeholder="Enter your name"
                            />


                            <MyTextInput
                                label="Email"
                                name="email"
                                type="text"
                                placeholder="Enter your email"
                            />
                            <MyTextInput
                                label="Age"
                                name="age"
                                type="number"
                                placeholder="16"
                            />

                            <MySelect label="Gender" name="gender">
                                <option value="">Select a gender</option>
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                            </MySelect>

                            <Button
                                disabled={!isValid || isSubmitting}
                                type="submit" mt={2}>Submit</Button>
                        </Stack>
                    </Form>
                )}
            </Formik>
        </>
    );
};

export default CreateCustomerForm;
```

### step 379:

refer https://v2.chakra-ui.com/docs/components/toast

add these to main.jsx and create a new file name notifcation.js
under services

main.jsx:

```jsx
import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import {ChakraProvider} from '@chakra-ui/react'
import App from './Customer.jsx'
import {createStandaloneToast} from '@chakra-ui/react'

const {ToastContainer} = createStandaloneToast()

createRoot(document.getElementById('root')).render(
    <StrictMode>
        <ChakraProvider>
            <App/>
            <ToastContainer/>
        </ChakraProvider>
    </StrictMode>,
)

```

notification.js:

```js
import {createStandaloneToast} from "@chakra-ui/react";

const {toast} = createStandaloneToast()

const notification = (title, description, status) => {
    toast(
        {
            title,
            description,
            status,
            isClosable: true,
            duration: 3000
        }
    )
}

export const successNotification = (title, description) => {
    notification(
        title,
        description,
        "success"
    )
}
export const errorNotification = (title, description) => {
    notification(
        title,
        description,
        "error"
    )
}

```

### step 380

added the notification when customer saved and close the form automatically and refresh the page
Customer.jsx

```jsx
import {
    Wrap,
    WrapItem,
    Spinner,
    Text,
    Table,
    Thead,
    Tbody,
    Tfoot,
    Tr,
    Th,
    Td,
    TableCaption,
    TableContainer
} from '@chakra-ui/react';
import SidebarWithHeader from "./components/shared/SideBar";
import {useEffect, useState} from "react";
import {getCustomers} from "./services/client.js";
import CardWithImage from "./components/Card";
import CreateCustomerDrawer from "./components/CreateCustomerDrawer.jsx";
import {errorNotification} from "./services/notification.js";

const App = () => {

    const [customers, setCustomers] = useState([]);
    const [loading, setLoading] = useState(false);
    const [err, setError] = useState("");

    const fetchCustomers = () => {
        setLoading(true);
        getCustomers().then(res => {
            setCustomers(res.data);
        }).catch((err) => {
            setError(err.response.data.message)
            errorNotification(
                err.code,
                err.response.data.message)
        }).finally(() => {
            setLoading(false);
        });
    }
    useEffect(() => {
        fetchCustomers();
    }, []);

    if (loading) {
        return (
            <SidebarWithHeader>
                <Spinner
                    thickness='4px'
                    speed='0.65s'
                    emptyColor='gray.200'
                    color='blue.500'
                    size='xl'
                />
            </SidebarWithHeader>
        );
    }

    if (customers.length <= 0) {
        return (
            <SidebarWithHeader>
                <CreateCustomerDrawer
                    fetchCustomers={fetchCustomers}
                />
                <Text mt={5}>
                    No customers available.
                </Text>
            </SidebarWithHeader>
        );
    }

    if (err) {
        return (
            <SidebarWithHeader>
                <CreateCustomerDrawer
                    fetchCustomers={fetchCustomers}
                />
                <Text mt={5}>
                    Oops there was an error
                </Text>
            </SidebarWithHeader>
        );
    }
    return (
        <SidebarWithHeader>
            <CreateCustomerDrawer
                fetchCustomers={fetchCustomers}
            />
            <Wrap justify='center' spacing='30px'>
                {customers.map((customer, index) => (
                    <WrapItem key={customer.id}> {/* Added key prop */}
                        <CardWithImage {...customer}
                                       imageNumber={index}/>
                    </WrapItem>
                ))}
            </Wrap>
        </SidebarWithHeader>
    );
}

export default App;

```

CreateCustomerDrawer.jsx:

```jsx
import {
    Button,
    Drawer,
    DrawerBody,
    DrawerCloseButton, DrawerContent, DrawerFooter,
    DrawerHeader,
    DrawerOverlay, Input,
    useDisclosure
} from "@chakra-ui/react";
import CreateCustomerForm from "./CreateCustomerForm.jsx";

const AddIcon = () => "+";
const CloseIcon = () => "X";

const CreateCustomerDrawer = ({fetchCustomers}) => {
    const {isOpen, onOpen, onClose} = useDisclosure();

    return (
        <>
            <Button leftIcon={<AddIcon/>} colorScheme="teal" onClick={onOpen}>
                Create Customer
            </Button>
            <Drawer isOpen={isOpen} onClose={onClose} size="xl">
                <DrawerOverlay/>
                <DrawerContent>
                    <DrawerCloseButton/>
                    <DrawerHeader>Create New Customer</DrawerHeader>
                    <DrawerBody>
                        <CreateCustomerForm fetchCustomers={fetchCustomers}
                                            onClose={onClose}/> {/* Pass onClose here */}
                    </DrawerBody>
                    <DrawerFooter>
                        <Button leftIcon={<CloseIcon/>} colorScheme="teal" onClick={onClose}>
                            Close
                        </Button>
                    </DrawerFooter>
                </DrawerContent>
            </Drawer>
        </>
    );
};


export default CreateCustomerDrawer;

```

CreateCustomerForm.jsx:

```jsx
import {Formik, Form, useField} from 'formik';
import * as Yup from 'yup';
import {Alert, AlertIcon, Box, Button, FormLabel, Input, Select, Stack} from "@chakra-ui/react";
import {saveCustomer} from "../services/client.js";
import {errorNotification, successNotification} from "../services/notification.js";

const MyTextInput = ({label, ...props}) => {
    // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
    // which we can spread on <input>. We can use field meta to show an error
    // message if the field is invalid, and it has been touched (i.e. visited)
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={"2"}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

const MySelect = ({label, ...props}) => {
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Select {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={"2"}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

// And now we can use these
const CreateCustomerForm = ({fetchCustomers, onClose}) => {
    return (
        <>
            <Formik
                initialValues={{
                    name: '',
                    email: '',
                    age: 0,
                    gender: '',
                }}
                validationSchema={Yup.object({
                    name: Yup.string()
                        .max(20, 'Must be 20 characters or less')
                        .required('Required'),
                    email: Yup.string()
                        .email('Invalid email address')
                        .required('Required'),
                    age: Yup.number()
                        .integer('Age must be a whole number')
                        .min(16, 'Must be at least 16 years of age')
                        .max(100, 'Must be less than 100 years of age')
                        .required('Required'),
                    gender: Yup.string()
                        .oneOf(['MALE', 'FEMALE'], 'Invalid gender')
                        .required('Required'),
                })}
                onSubmit={(customer, {setSubmitting}) => {
                    setSubmitting(true);
                    saveCustomer(customer)
                        .then(res => {
                            successNotification(
                                "Customer Saved",
                                `${customer.name} was successfully saved`
                            );
                            fetchCustomers(); // Refresh customer list
                            onClose(); // Close the drawer upon success
                        })
                        .catch(err => {
                            errorNotification(
                                err.code,
                                err.response.data.message
                            );
                        })
                        .finally(() => {
                            setSubmitting(false);
                        });
                }}
            >
                {({isValid, isSubmitting}) => (
                    <Form>
                        <Stack spacing="24px">
                            <MyTextInput label="Name" name="name" type="text" placeholder="Enter your name"/>
                            <MyTextInput label="Email" name="email" type="text" placeholder="Enter your email"/>
                            <MyTextInput label="Age" name="age" type="number" placeholder="16"/>
                            <MySelect label="Gender" name="gender">
                                <option value="">Select a gender</option>
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                            </MySelect>
                            <Button
                                disabled={!isValid || isSubmitting}
                                type="submit" mt={2}>
                                Submit
                            </Button>
                        </Stack>
                    </Form>
                )}
            </Formik>
        </>
    );
};

export default CreateCustomerForm;
```

### step 381:

exercise add a delete button to delete the cutsomer

### step 382:

add delete button in card.jsx and pass the fetch customers from APp.jsx
Customer.jsx:

```jsx
import {
    Wrap,
    WrapItem,
    Spinner,
    Text,
    Table,
    Thead,
    Tbody,
    Tfoot,
    Tr,
    Th,
    Td,
    TableCaption,
    TableContainer
} from '@chakra-ui/react';
import SidebarWithHeader from "./components/shared/SideBar";
import {useEffect, useState} from "react";
import {getCustomers} from "./services/client.js";
import CardWithImage from "./components/Card";
import CreateCustomerDrawer from "./components/CreateCustomerDrawer.jsx";
import {errorNotification} from "./services/notification.js";

const App = () => {

    const [customers, setCustomers] = useState([]);
    const [loading, setLoading] = useState(false);
    const [err, setError] = useState("");

    const fetchCustomers = () => {
        setLoading(true);
        getCustomers().then(res => {
            setCustomers(res.data);
        }).catch((err) => {
            setError(err.response.data.message)
            errorNotification(
                err.code,
                err.response.data.message)
        }).finally(() => {
            setLoading(false);
        });
    }
    useEffect(() => {
        fetchCustomers();
    }, []);

    if (loading) {
        return (
            <SidebarWithHeader>
                <Spinner
                    thickness='4px'
                    speed='0.65s'
                    emptyColor='gray.200'
                    color='blue.500'
                    size='xl'
                />
            </SidebarWithHeader>
        );
    }

    if (customers.length <= 0) {
        return (
            <SidebarWithHeader>
                <CreateCustomerDrawer
                    fetchCustomers={fetchCustomers}
                />
                <Text mt={5}>
                    No customers available.
                </Text>
            </SidebarWithHeader>
        );
    }

    if (err) {
        return (
            <SidebarWithHeader>
                <CreateCustomerDrawer
                    fetchCustomers={fetchCustomers}
                />
                <Text mt={5}>
                    Oops there was an error
                </Text>
            </SidebarWithHeader>
        );
    }
    return (
        <SidebarWithHeader>
            <CreateCustomerDrawer
                fetchCustomers={fetchCustomers}
            />
            <Wrap justify='center' spacing='30px'>
                {customers.map((customer, index) => (
                    <WrapItem key={customer.id}> {/* Added key prop */}
                        <CardWithImage
                            {...customer}
                            imageNumber={index}
                            fetchCustomers={fetchCustomers}

                        />
                    </WrapItem>
                ))}
            </Wrap>
        </SidebarWithHeader>
    );
}

export default App;

```

CustomerCard.jsx:

```jsx
import {
    Heading,
    Avatar,
    Box,
    Center,
    Image,
    Flex,
    Text,
    Stack,
    useColorModeValue,
    Tag,
    Button,
    useDisclosure,
    AlertDialogFooter,
    AlertDialog,
    AlertDialogOverlay,
    AlertDialogContent, AlertDialogBody, AlertDialogHeader,
} from '@chakra-ui/react'
import {useRef} from "react";
import {deleteCustomer} from "../services/client.js";
import {errorNotification, successNotification} from "../services/notification.js";

export default function CardWithImage({id, email, image, age, name, gender, imageNumber, fetchCustomers}) { // Destructure props correctly
    const randomUserGender = gender === "MALE" ? "men" : "women";
    const {isOpen, onOpen, onClose} = useDisclosure();
    const cancelRef = useRef();
    return (
        <Center py={6}>
            <Box
                maxW={'300px'}
                w={'full'}
                bg={useColorModeValue('white', 'gray.800')}
                boxShadow={'2xl'}
                rounded={'md'}
                overflow={'hidden'}>
                <Image
                    h={'120px'}
                    w={'full'}
                    src={
                        image || 'https://images.unsplash.com/photo-1612865547334-09cb8cb455da?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80' // Use the image prop or default
                    }
                    objectFit="cover"
                    alt="Card Image"
                />
                <Flex justify={'center'} mt={-12}>
                    <Avatar
                        size={'xl'}
                        src={
                            `https://randomuser.me/api/portraits/${randomUserGender}/${imageNumber}.jpg`
                        }
                        css={{
                            border: '2px solid white',
                        }}
                    />
                </Flex>


                <Box p={6}>
                    <Stack spacing={2} align={'center'} mb={5}>
                        <Tag borderRadius='full'>{id}</Tag>
                        <Heading fontSize={'2xl'} fontWeight={500} fontFamily={'body'}>
                            {name || 'Anonymous'} {/* Add name or fallback to 'Anonymous' */}
                        </Heading>
                        <Text color={'gray.500'}>{email}</Text>
                        <Text color={'gray.500'}>Age: {age} | gender: {gender}</Text>

                    </Stack>
                </Box>
                <Stack m={8}>
                    <Button
                        mt={8}
                        bg={'red.400'}
                        color={'white'}
                        rounded={'full'}
                        _hover={{
                            transform: 'translateY(-2px)',
                            boxShadow: 'lg'
                        }}
                        _focus={{
                            bg: 'green.500'
                        }}

                        onClick={onOpen}
                    >
                        Delete
                    </Button>
                    <AlertDialog
                        isOpen={isOpen}
                        leastDestructiveRef={cancelRef}
                        onClose={onClose}
                    >
                        <AlertDialogOverlay>
                            <AlertDialogContent>
                                <AlertDialogHeader fontSize='lg' fontWeight='bold'>
                                    Delete Customer
                                </AlertDialogHeader>

                                <AlertDialogBody>
                                    Are you sure want to delet {name}? You can't undo this action afterwards.
                                </AlertDialogBody>

                                <AlertDialogFooter>
                                    <Button ref={cancelRef} onClick={onClose}>
                                        Cancel
                                    </Button>
                                    <Button colorScheme='red' onClick={() => {
                                        deleteCustomer(id)
                                            .then(res => {
                                                successNotification(
                                                    'Customer deleted',
                                                    `${name} was successfully deleted`
                                                );
                                                onClose();
                                                fetchCustomers();

                                            }).catch(err => {
                                            errorNotification(
                                                err.code,
                                                err.response.data.message)
                                        }).finally(() => {
                                                onClose();
                                            }
                                        )

                                    }} ml={3}>
                                        Delete
                                    </Button>
                                </AlertDialogFooter>
                            </AlertDialogContent>
                        </AlertDialogOverlay>
                    </AlertDialog>
                </Stack>
            </Box>
        </Center>
    );
}

```

### step 383

exercise add edit and edit the details

### step 384:

app.jsx:

```jsx
import {
    Wrap,
    WrapItem,
    Spinner,
    Text,
    Table,
    Thead,
    Tbody,
    Tfoot,
    Tr,
    Th,
    Td,
    TableCaption,
    TableContainer
} from '@chakra-ui/react';
import SidebarWithHeader from "./components/shared/SideBar";
import {useEffect, useState} from "react";
import {getCustomers} from "./services/client.js";
import CardWithImage from "./components/Card";
import CreateCustomerDrawer from "./components/CreateCustomerDrawer.jsx";
import {errorNotification} from "./services/notification.js";

const App = () => {

    const [customers, setCustomers] = useState([]);
    const [loading, setLoading] = useState(false);
    const [err, setError] = useState("");

    const fetchCustomers = () => {
        setLoading(true);
        getCustomers().then(res => {
            setCustomers(res.data);
        }).catch((err) => {
            setError(err.response.data.message)
            errorNotification(
                err.code,
                err.response.data.message)
        }).finally(() => {
            setLoading(false);
        });
    }
    useEffect(() => {
        fetchCustomers();
    }, []);

    if (loading) {
        return (
            <SidebarWithHeader>
                <Spinner
                    thickness='4px'
                    speed='0.65s'
                    emptyColor='gray.200'
                    color='blue.500'
                    size='xl'
                />
            </SidebarWithHeader>
        );
    }


    if (err) {
        return (
            <SidebarWithHeader>
                <CreateCustomerDrawer
                    fetchCustomers={fetchCustomers}
                />
                <Text mt={5}>
                    Oops there was an error
                </Text>
            </SidebarWithHeader>
        );
    }

    if (customers.length <= 0) {
        return (
            <SidebarWithHeader>
                <CreateCustomerDrawer
                    fetchCustomers={fetchCustomers}
                />
                <Text mt={5}>
                    No customers available.
                </Text>
            </SidebarWithHeader>
        );
    }

    return (
        <SidebarWithHeader>
            <CreateCustomerDrawer
                fetchCustomers={fetchCustomers}
            />
            <Wrap justify='center' spacing='30px'>
                {customers.map((customer, index) => (
                    <WrapItem key={index}> {/* Added key prop */}
                        <CardWithImage
                            {...customer}
                            imageNumber={index}
                            fetchCustomers={fetchCustomers}

                        />
                    </WrapItem>
                ))}
            </Wrap>
        </SidebarWithHeader>
    );
}

export default App;

```

Client.js:

```js
import axios from 'axios';

export const getCustomers = async () => {
    try {
        return await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`);
    } catch (e) {
        throw e;
    }
}

export const saveCustomer = async (customer) => {
    try {
        return await axios.post(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`,
            customer // <-- move this inside the axios.post() function call
        );
    } catch (e) {
        throw e;
    }
}
export const deleteCustomer = async (id) => {
    try {
        return await axios.delete(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${id}`
        );
    } catch (e) {
        throw e;
    }
}
export const updateCustomer = async (id, update) => {
    try {
        return await axios.put(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${id}`,
            update
        );
    } catch (e) {
        throw e;
    }
}


```

UpdateCustomerDrawer.jsx:

```jsx
import {
    Button,
    Drawer,
    DrawerBody,
    DrawerCloseButton, DrawerContent, DrawerFooter,
    DrawerHeader,
    DrawerOverlay, Input,
    useDisclosure
} from "@chakra-ui/react";
import CreateCustomerForm from "./CreateCustomerForm.jsx";
import UpdateCustomerForm from "./UpdateCustomerForm.jsx";

const AddIcon = () => "+";
const CloseIcon = () => "X";

const UpdateCustomerDrawer = ({fetchCustomers, initialValues, customerId}) => {
    const {isOpen, onOpen, onClose} = useDisclosure();

    return (
        <>
            <Button
                bg={'gray.200'}
                color={'black'}
                rounded={'full'}
                _hover={{
                    transform: 'translateY(-2px)',
                    boxShadow: 'lg'
                }}
                onClick={onOpen}
            >
                Update Customer
            </Button>
            <Drawer isOpen={isOpen} onClose={onClose} size="xl">
                <DrawerOverlay/>
                <DrawerContent>
                    <DrawerCloseButton/>
                    <DrawerHeader>Update Customer</DrawerHeader>
                    <DrawerBody>
                        <UpdateCustomerForm
                            fetchCustomers={fetchCustomers}
                            initialValues={initialValues}
                            customerId={customerId}
                            onClose={onClose}
                        />
                    </DrawerBody>
                    <DrawerFooter>
                        <Button leftIcon={<CloseIcon/>} colorScheme="teal" onClick={onClose}>
                            Close
                        </Button>
                    </DrawerFooter>
                </DrawerContent>
            </Drawer>
        </>
    );
};


export default UpdateCustomerDrawer;

```

UpdateCustomerForm.jsx:

```jsx
import {Formik, Form, useField} from 'formik';
import * as Yup from 'yup';
import {Alert, AlertIcon, Box, Button, FormLabel, Input, Select, Stack} from "@chakra-ui/react";
import {saveCustomer, updateCustomer} from "../services/client.js";
import {errorNotification, successNotification} from "../services/notification.js";

const MyTextInput = ({label, ...props}) => {
    // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
    // which we can spread on <input>. We can use field meta to show an error
    // message if the field is invalid, and it has been touched (i.e. visited)
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={"2"}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

// And now we can use these
const UpdateCustomerForm = ({fetchCustomers, initialValues, customerId, onClose}) => {
    return (
        <>
            <Formik
                initialValues={initialValues}
                validationSchema={Yup.object({
                    name: Yup.string()
                        .max(20, 'Must be 20 characters or less')
                        .required('Required'),
                    email: Yup.string()
                        .email('Invalid email address')
                        .required('Required'),
                    age: Yup.number()
                        .integer('Age must be a whole number')
                        .min(16, 'Must be at least 16 years of age')
                        .max(100, 'Must be less than 100 years of age')
                        .required('Required'),
                })}
                onSubmit={(updatedCustomer, {setSubmitting}) => {
                    setSubmitting(true);
                    updateCustomer(customerId, updatedCustomer)
                        .then(res => {
                            console.log(res);
                            successNotification(
                                "Customer Updated",
                                `${updatedCustomer.name} was successfully Updated`
                            );
                            onClose();
                            fetchCustomers();

                        }).catch(err => {
                        console.log(err);
                        errorNotification(
                            err.code,
                            err.response.data.message || 'Failed to update customer'
                        );
                    }).finally(() => {
                        setSubmitting(false);
                    });
                }}
            >
                {({isValid, isSubmitting, dirty}) => (
                    <Form>
                        <Stack spacing="24px">
                            <MyTextInput label="Name" name="name" type="text" placeholder="Enter your name"/>
                            <MyTextInput label="Email" name="email" type="text" placeholder="Enter your email"/>
                            <MyTextInput label="Age" name="age" type="number" placeholder="16"/>
                            <Button
                                disabled={!(isValid && dirty) || isSubmitting}
                                type="submit" mt={2}>
                                Submit
                            </Button>
                        </Stack>
                    </Form>
                )}
            </Formik>
        </>
    );
};

export default UpdateCustomerForm;
```

CustomerCard.jsx:

```jsx
import {
    Heading,
    Avatar,
    Box,
    Center,
    Image,
    Flex,
    Text,
    Stack,
    useColorModeValue,
    Tag,
    Button,
    useDisclosure,
    AlertDialogFooter,
    AlertDialog,
    AlertDialogOverlay,
    AlertDialogContent, AlertDialogBody, AlertDialogHeader,
} from '@chakra-ui/react'
import {useRef} from "react";
import {deleteCustomer} from "../services/client.js";
import {errorNotification, successNotification} from "../services/notification.js";
import UpdateCustomerDrawer from "./UpdateCustomerDrawer.jsx";

export default function CardWithImage({id, name, email, age, gender, imageNumber, fetchCustomers}) { // Destructure props correctly
    const randomUserGender = gender === "MALE" ? "men" : "women";

    const {isOpen, onOpen, onClose} = useDisclosure();
    const cancelRef = useRef();

    return (
        <Center py={6}>
            <Box
                maxW={'300px'}
                w={'full'}
                bg={useColorModeValue('white', 'gray.800')}
                boxShadow={'2xl'}
                rounded={'md'}
                overflow={'hidden'}>
                <Image
                    h={'120px'}
                    w={'full'}
                    src={
                        'https://images.unsplash.com/photo-1612865547334-09cb8cb455da?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80' // Use the image prop or default
                    }
                    objectFit={'cover'}
                />
                <Flex justify={'center'} mt={-12}>
                    <Avatar
                        size={'xl'}
                        src={
                            `https://randomuser.me/api/portraits/${randomUserGender}/${imageNumber}.jpg`
                        }
                        alt={'Author'}
                        css={{
                            border: '2px solid white',
                        }}
                    />
                </Flex>


                <Box p={6}>
                    <Stack spacing={2} align={'center'} mb={5}>
                        <Tag borderRadius={'full'}>{id}</Tag>
                        <Heading fontSize={'2xl'} fontWeight={500} fontFamily={'body'}>
                            {name}
                        </Heading>
                        <Text color={'gray.500'}>{email}</Text>
                        <Text color={'gray.500'}>Age: {age} | gender: {gender}</Text>

                    </Stack>
                </Box>
                <Stack direction={'row'} justify={'center'} spacing={6}>
                    <Stack>
                        <UpdateCustomerDrawer
                            initialValues={{name, email, age}}
                            customerId={id}
                            fetchCustomers={fetchCustomers}
                        />
                    </Stack>
                    <Stack>
                        <Button
                            bg={'red.400'}
                            color={'white'}
                            rounded={'full'}
                            _hover={{
                                transform: 'translateY(-2px)',
                                boxShadow: 'lg'
                            }}
                            _focus={{
                                bg: 'green.500'
                            }}
                            onClick={onOpen}
                        >
                            Delete
                        </Button>
                        <AlertDialog
                            isOpen={isOpen}
                            leastDestructiveRef={cancelRef}
                            onClose={onClose}
                        >
                            <AlertDialogOverlay>
                                <AlertDialogContent>
                                    <AlertDialogHeader fontSize='lg' fontWeight='bold'>
                                        Delete Customer
                                    </AlertDialogHeader>

                                    <AlertDialogBody>
                                        Are you sure want to delet {name}? You can't undo this action afterwards.
                                    </AlertDialogBody>

                                    <AlertDialogFooter>
                                        <Button ref={cancelRef} onClick={onClose}>
                                            Cancel
                                        </Button>
                                        <Button colorScheme='red' onClick={() => {
                                            deleteCustomer(id)
                                                .then(res => {
                                                    successNotification(
                                                        'Customer deleted',
                                                        `${name} was successfully deleted`
                                                    );
                                                    onClose();
                                                    fetchCustomers();

                                                }).catch(err => {
                                                errorNotification(
                                                    err.code,
                                                    err.response.data.message)
                                            }).finally(() => {
                                                    onClose();
                                                }
                                            )

                                        }} ml={3}>
                                            Delete
                                        </Button>
                                    </AlertDialogFooter>
                                </AlertDialogContent>
                            </AlertDialogOverlay>
                        </AlertDialog>
                    </Stack>
                </Stack>
            </Box>
        </Center>
    );
}

```

### step 385:

make it look better:
CustomerCard.jsx:

```jsx
import {
    Heading,
    Avatar,
    Box,
    Center,
    Image,
    Flex,
    Text,
    Stack,
    useColorModeValue,
    Tag,
    Button,
    useDisclosure,
    AlertDialogFooter,
    AlertDialog,
    AlertDialogOverlay,
    AlertDialogContent, AlertDialogBody, AlertDialogHeader,
} from '@chakra-ui/react'
import {useRef} from "react";
import {deleteCustomer} from "../services/client.js";
import {errorNotification, successNotification} from "../services/notification.js";
import UpdateCustomerDrawer from "./UpdateCustomerDrawer.jsx";

export default function CardWithImage({id, name, email, age, gender, imageNumber, fetchCustomers}) { // Destructure props correctly
    const randomUserGender = gender === "MALE" ? "men" : "women";

    const {isOpen, onOpen, onClose} = useDisclosure();
    const cancelRef = useRef();

    return (
        <Center py={6}>
            <Box
                maxW={'300px'}
                minW={'300px'}
                w={'full'}
                m={2}
                bg={useColorModeValue('white', 'gray.800')}
                boxShadow={'lg'}
                rounded={'md'}
                overflow={'hidden'}>
                <Image
                    h={'120px'}
                    w={'full'}
                    src={
                        'https://images.unsplash.com/photo-1612865547334-09cb8cb455da?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80' // Use the image prop or default
                    }
                    objectFit={'cover'}
                />
                <Flex justify={'center'} mt={-12}>
                    <Avatar
                        size={'xl'}
                        src={
                            `https://randomuser.me/api/portraits/${randomUserGender}/${imageNumber}.jpg`
                        }
                        alt={'Author'}
                        css={{
                            border: '2px solid white',
                        }}
                    />
                </Flex>


                <Box p={6}>
                    <Stack spacing={2} align={'center'} mb={5}>
                        <Tag borderRadius={'full'}>{id}</Tag>
                        <Heading fontSize={'2xl'} fontWeight={500} fontFamily={'body'}>
                            {name}
                        </Heading>
                        <Text color={'gray.500'}>{email}</Text>
                        <Text color={'gray.500'}>Age: {age} | gender: {gender}</Text>

                    </Stack>
                </Box>
                <Stack direction={'row'} justify={'center'} spacing={6} p={4}>
                    <Stack>
                        <UpdateCustomerDrawer
                            initialValues={{name, email, age}}
                            customerId={id}
                            fetchCustomers={fetchCustomers}
                        />
                    </Stack>
                    <Stack>
                        <Button
                            bg={'red.400'}
                            color={'white'}
                            rounded={'full'}
                            _hover={{
                                transform: 'translateY(-2px)',
                                boxShadow: 'lg'
                            }}
                            _focus={{
                                bg: 'green.500'
                            }}
                            onClick={onOpen}
                        >
                            Delete
                        </Button>
                        <AlertDialog
                            isOpen={isOpen}
                            leastDestructiveRef={cancelRef}
                            onClose={onClose}
                        >
                            <AlertDialogOverlay>
                                <AlertDialogContent>
                                    <AlertDialogHeader fontSize='lg' fontWeight='bold'>
                                        Delete Customer
                                    </AlertDialogHeader>

                                    <AlertDialogBody>
                                        Are you sure want to delet {name}? You can't undo this action afterwards.
                                    </AlertDialogBody>

                                    <AlertDialogFooter>
                                        <Button ref={cancelRef} onClick={onClose}>
                                            Cancel
                                        </Button>
                                        <Button colorScheme='red' onClick={() => {
                                            deleteCustomer(id)
                                                .then(res => {
                                                    successNotification(
                                                        'Customer deleted',
                                                        `${name} was successfully deleted`
                                                    );
                                                    onClose();
                                                    fetchCustomers();

                                                }).catch(err => {
                                                errorNotification(
                                                    err.code,
                                                    err.response.data.message)
                                            }).finally(() => {
                                                    onClose();
                                                }
                                            )

                                        }} ml={3}>
                                            Delete
                                        </Button>
                                    </AlertDialogFooter>
                                </AlertDialogContent>
                            </AlertDialogOverlay>
                        </AlertDialog>
                    </Stack>
                </Stack>
            </Box>
        </Center>
    );
}

```

Sidebar.jsx:

```jsx
'use client'

import {
    IconButton,
    Avatar,
    Box,
    CloseButton,
    Flex,
    HStack,
    VStack,
    Icon,
    useColorModeValue,
    Text,
    Drawer,
    DrawerContent,
    useDisclosure,
    Menu,
    MenuButton,
    MenuDivider,
    MenuItem,
    MenuList, Image,
} from '@chakra-ui/react'
import {
    FiHome,
    FiTrendingUp,
    FiCompass,
    FiStar,
    FiSettings,
    FiMenu,
    FiBell,
    FiChevronDown,
} from 'react-icons/fi'

const LinkItems = [
    {name: 'Home', icon: FiHome},
    {name: 'Trending', icon: FiTrendingUp},
    {name: 'Explore', icon: FiCompass},
    {name: 'Favourites', icon: FiStar},
    {name: 'Settings', icon: FiSettings},
]

const SidebarContent = ({onClose, ...rest}) => {
    return (
        <Box
            transition="3s ease"
            bg={useColorModeValue('white', 'gray.900')}
            borderRight="1px"
            borderRightColor={useColorModeValue('gray.200', 'gray.700')}
            w={{base: 'full', md: 60}}
            pos="fixed"
            h="full"
            {...rest}>
            <Flex h="20" flexDirection="column" alignItems="center" mx="8" mb={70} mt={2}
                  justifyContent="space-between">
                <Text fontSize="2xl" fontFamily="monospace" fontWeight="bold" mb={5}>
                    Dashboard
                </Text>
                <Image
                    borderRadius='full'
                    boxSize='75px'
                    src='https://bit.ly/dan-abramov'
                    alt='Dan Abramov'
                />
                <CloseButton display={{base: 'flex', md: 'none'}} onClick={onClose}/>
            </Flex>
            {LinkItems.map((link) => (
                <NavItem key={link.name} icon={link.icon}>
                    {link.name}
                </NavItem>
            ))}
        </Box>
    )
}

const NavItem = ({icon, children, ...rest}) => {
    return (
        <Box
            as="a"
            href="#"
            style={{textDecoration: 'none'}}
            _focus={{boxShadow: 'none'}}>
            <Flex
                align="center"
                p="4"
                mx="4"
                borderRadius="lg"
                role="group"
                cursor="pointer"
                _hover={{
                    bg: 'red.400',
                    color: 'white',
                }}
                {...rest}>
                {icon && (
                    <Icon
                        mr="4"
                        fontSize="16"
                        _groupHover={{
                            color: 'white',
                        }}
                        as={icon}
                    />
                )}
                {children}
            </Flex>
        </Box>
    )
}

const MobileNav = ({onOpen, ...rest}) => {
    return (
        <Flex
            ml={{base: 0, md: 60}}
            px={{base: 4, md: 4}}
            height="20"
            alignItems="center"
            bg={useColorModeValue('white', 'gray.900')}
            borderBottomWidth="1px"
            borderBottomColor={useColorModeValue('gray.200', 'gray.700')}
            justifyContent={{base: 'space-between', md: 'flex-end'}}
            {...rest}>
            <IconButton
                display={{base: 'flex', md: 'none'}}
                onClick={onOpen}
                variant="outline"
                aria-label="open menu"
                icon={<FiMenu/>}
            />

            <Text
                display={{base: 'flex', md: 'none'}}
                fontSize="2xl"
                fontFamily="monospace"
                fontWeight="bold">
                Logo
            </Text>

            <HStack spacing={{base: '0', md: '6'}}>
                <IconButton size="lg" variant="ghost" aria-label="open menu" icon={<FiBell/>}/>
                <Flex alignItems={'center'}>
                    <Menu>
                        <MenuButton py={2} transition="all 0.3s" _focus={{boxShadow: 'none'}}>
                            <HStack>
                                <Avatar
                                    size={'sm'}
                                    src={
                                        'https://images.unsplash.com/photo-1619946794135-5bc917a27793?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=200&w=200&s=b616b2c5b373a80ffc9636ba24f7a4a9'
                                    }
                                />
                                <VStack
                                    display={{base: 'none', md: 'flex'}}
                                    alignItems="flex-start"
                                    spacing="1px"
                                    ml="2">
                                    <Text fontSize="sm">Justina Clark</Text>
                                    <Text fontSize="xs" color="gray.600">
                                        Admin
                                    </Text>
                                </VStack>
                                <Box display={{base: 'none', md: 'flex'}}>
                                    <FiChevronDown/>
                                </Box>
                            </HStack>
                        </MenuButton>
                        <MenuList
                            bg={useColorModeValue('white', 'gray.900')}
                            borderColor={useColorModeValue('gray.200', 'gray.700')}>
                            <MenuItem>Profile</MenuItem>
                            <MenuItem>Settings</MenuItem>
                            <MenuItem>Billing</MenuItem>
                            <MenuDivider/>
                            <MenuItem>Sign out</MenuItem>
                        </MenuList>
                    </Menu>
                </Flex>
            </HStack>
        </Flex>
    )
}

const SidebarWithHeader = ({children}) => {
    const {isOpen, onOpen, onClose} = useDisclosure()

    return (
        <Box minH="100vh" bg={useColorModeValue('gray.100', 'gray.900')}>
            <SidebarContent onClose={() => onClose} display={{base: 'none', md: 'block'}}/>
            <Drawer
                isOpen={isOpen}
                placement="left"
                onClose={onClose}
                returnFocusOnClose={false}
                onOverlayClick={onClose}
                size="full">
                <DrawerContent>
                    <SidebarContent onClose={onClose}/>
                </DrawerContent>
            </Drawer>
            {/* mobilenav */}
            <MobileNav onOpen={onOpen}/>
            <Box ml={{base: 0, md: 60}} p="4">
                {children} {/* Content */}
            </Box>
        </Box>
    )
}

export default SidebarWithHeader

```

### step 386:

we are going to deploy our front end

### step 387:

create two files:
Dockerfile under react folder
.dockerignore under react folder
Dockerfile:

```dockerfile

FROM node:23-alpine
WORKDIR /app
COPY package*.json .
RUN npm i --silent
COPY . .
RUN echo "VITE_API_BASE_URL=http://localhost:8088" .env
EXPOSE 5173
CMD ["npm","run","dev"]
```

.dockerignore

```text
node_modules
Dockerfile
.env
```

### step 388:

open the React folder in git
to create image

```bash
docker build . -t dkedarnath/kedarnath-react
```

to push the image

```bash
docker push dkedarnath/kedarnath-react
```

### step 389:

we get issues i dont know why, but I have updated the docker file to make it work

Dockerfilr:

```dockerfile
FROM node:23-alpine
WORKDIR /app
COPY package*.json .
RUN npm i --silent
COPY . .
RUN echo "VITE_API_BASE_URL=http://host.docker.internal:8088" > .env
EXPOSE 5173
CMD ["npm","run","dev"]
```

```bash
docker run --rm --name frontend-react -p 3000:5173 dkedarnath/kedarnath-react
```

update package.json:
package.json
"dev": "vite --host", this line is updated

```json
{
  "name": "react",
  "private": true,
  "version": "0.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite --host",
    "build": "vite build",
    "lint": "eslint .",
    "preview": "vite preview"
  },
  "dependencies": {
    "@chakra-ui/react": "^2.10.2",
    "@emotion/react": "^11.13.3",
    "@emotion/styled": "^11.13.0",
    "axios": "^1.7.7",
    "formik": "^2.4.6",
    "framer-motion": "^11.11.8",
    "react": "^18.3.1",
    "react-dom": "^18.3.1",
    "react-icons": "^5.3.0",
    "yup": "^1.4.0"
  },
  "devDependencies": {
    "@eslint/js": "^9.11.1",
    "@types/react": "^18.3.10",
    "@types/react-dom": "^18.3.0",
    "@vitejs/plugin-react": "^4.3.2",
    "eslint": "^9.11.1",
    "eslint-plugin-react": "^7.37.0",
    "eslint-plugin-react-hooks": "^5.1.0-rc.0",
    "eslint-plugin-react-refresh": "^0.4.12",
    "globals": "^15.9.0",
    "vite": "^5.4.8"
  }
}

```

### step 390: 19th oct 24

```bash
docker run -d --name frontend-react -p 3000:5173 dkedarnath/kedarnath-react
```

```bash
docker exec -it frontend-react sh
pwd
ls - a
cat .env

```

ctrl + d - to come out

### step 391:

we are going to add the front end to docker compose.yml to test the applcation

```
docker rm -f frontend-react
```

got to main folder

```
ls
docker compose up -d
docker compose down
```

### step 392:

deploy frontend to ec2 in next steps

### step 393:

update the docker-compose.yml

```yaml
services:
  db:
    container_name: postgres
    image: postgres:16
    environment:
      POSTGRES_USER: kedarnath
      POSTGRES_PASSWORD: password
      POSTGRES_DB: customer
      PGDATA: /data/postgres
    volumes:
      - db:/data/postgres
    ports:
      - "5432:5432"
    networks:
      - db
    restart: unless-stopped
  kedarnath-api:
    container_name: kedarnath-api
    image: dkedarnath/kedarnath-api
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/customer
    ports:
      - "8088:8080"
    networks:
      - db
    depends_on:
      - db
    restart: unless-stopped
  kedarnath-react:
    container_name: kedarnath-react
    image: dkedarnath/kedarnath-react
    build:
      context: frontend/react
      args:
        api_base_url: http://kedarnath-api:8088
    ports:
      - "3000:5173"
    depends_on:
      - kedarnath-api
    restart: unless-stopped

networks:
  db:
    driver: bridge

volumes:
  db:


```

```bash
    docker build . -t dkedarnath/kedarnath-react --build-arg <replace with elk url even bracts>:8080 
```

### step 394:

bring the aws.json file outside the backend and into the main folder

Dockerrun.aws.json:

```json
{
  "AWSEBDockerrunVersion": 2,
  "containerDefinitions": [
    {
      "name": "kedarnath-react",
      "image": "dkedarnath/kedarnath-react:latest",
      "essential": true,
      "memory": 256,
      "portMappings": [
        {
          "hostPort": 80,
          "containerPort": 5173
        }
      ],
      "healthCheck": {
        "command": [
          "CMD-SHELL",
          "curl -f http://localhost:8080/health || exit 1"
        ],
        "interval": 30,
        "timeout": 5,
        "retries": 3,
        "startPeriod": 60
      }
    },
    {
      "name": "kedarnath-api",
      "image": "dkedarnath/kedarnath-api:15.10.2024.12.33.00",
      "essential": true,
      "memory": 512,
      "portMappings": [
        {
          "hostPort": 8080,
          "containerPort": 8080
        }
      ],
      "environment": [
        {
          "name": "SPRING_DATASOURCE_URL",
          "value": "jdbc:postgresql://awseb-e-nskvm2faru-stack-awsebrdsdatabase-xn3ngsekufx0.c1qawm2oqlx3.us-east-1.rds.amazonaws.com:5432/customer"
        }
      ],
      "healthCheck": {
        "command": [
          "CMD-SHELL",
          "curl -f http://localhost:8080/health || exit 1"
        ],
        "interval": 30,
        "timeout": 5,
        "retries": 3,
        "startPeriod": 60
      }
    }
  ]
}
```

backend-cd.yml:

```yaml
name: CD - Deploy Backend

on:
  workflow_dispatch:
  push:
    branches:
      - main
    paths:
      - backend/**

jobs:
  build:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:16.3
        env:
          POSTGRES_USER: kedarnath
          POSTGRES_PASSWORD: password
          POSTGRES_DB: customer
        ports:
          - 5432:5432
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    defaults:
      run:
        working-directory: ./backend

    steps:
      - uses: actions/checkout@v4

      - name: Slack commit message and sha
        run: |
          curl -X POST -H 'Content-type: application/json' \
          --data '{"text":"https://github.com/kedarnathyadav/spring-boot-fullstack/commit/${{ github.sha }} - ${{ github.event.head_commit.message }}"}' \
          ${{secrets.SLACK_WEBHOOK_URL}}

      - name: List Files in Working Directory
        run: ls -la

      - name: Send Slack Message
        run: |
          curl -X POST -H 'Content-type: application/json' \
          --data '{"text":"Deployment started :progress_bar: :spring:"}' \
          ${{ secrets.SLACK_WEBHOOK_URL }}

      - uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
          cache: 'maven'

      - name: Send Slack Message
        run: |
          curl -X POST -H 'Content-type: application/json' \
          --data '{"text":":docker: Image tag:${{ steps.build-number.outputs.BUILD_NUMBER }} pushed to https://hub.docker.com/layers/dkedarnath/kedarnath-api/"}' \
          ${{ secrets.SLACK_WEBHOOK_URL }}

      - name: Login to Docker Hub
        uses: docker/login-action@v2
        with:
          username: ${{ secrets.DOCKERHUB_USERNAME }}
          password: ${{ secrets.DOCKERHUB_ACCESS_TOKEN }}

      - name: Set Build Number
        id: build-number
        run: echo "BUILD_NUMBER=$(date '+%d.%m.%Y.%H.%M.%S')" >> $GITHUB_OUTPUT

      - name: Send Slack Message
        run: |
          curl -X POST -H 'Content-type: application/json' \
          --data '{"text":"Building with Maven :maven:"}' \
          ${{ secrets.SLACK_WEBHOOK_URL }}

      - name: Build Package Push with Maven
        run: mvn -ntp -B verify -D docker.image.tag=${{ steps.build-number.outputs.BUILD_NUMBER }} jib:build

      - name: Update Dockerrun.aws.json api image tag with new build number
        run: |
          echo "Dockerrun.aws.json before updating the tag"
          cat ../Dockerrun.aws.json
          sed -i -E 's|(dkedarnath/kedarnath-api:)[^"]*|\1'${{ steps.build-number.outputs.BUILD_NUMBER }}'|' ../Dockerrun.aws.json
          echo "Dockerrun.aws.json after updating the tag"
          cat ../Dockerrun.aws.json

      - name: Send Slack Message
        run: |
          curl -X POST -H 'Content-type: application/json' \
          --data '{"text":":aws: Starting deployment to Elastic Beanstalk "}' \
          ${{ secrets.SLACK_WEBHOOK_URL }}

      - name: Deploy to EB
        uses: einaregilsson/beanstalk-deploy@v22
        with:
          aws_access_key: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws_secret_key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          application_name: ${{ secrets.EB_APPLICATION_NAME }}
          environment_name: ${{ secrets.EB_ENVIRONMENT_NAME }}
          version_label: ${{ steps.build-number.outputs.BUILD_NUMBER }}
          version_description: ${{ github.sha }}
          region: ${{ secrets.EB_REGION }}
          deployment_package: Dockerrun.aws.json

      - name: Send Slack Message
        run: |
          curl -X POST -H 'Content-type: application/json' \
          --data '{"text":":githubloading: Committing to repo  "}' \
          ${{ secrets.SLACK_WEBHOOK_URL }}

      - name: Commit and Push Dockerrun.aws.json
        run: |
          git config user.name github-actions
          git config user.email github-actions@github.com
          git add ../Dockerrun.aws.json
          git commit -m "Update Dockerrun.aws.json docker image with new tag ${{ steps.build-number.outputs.BUILD_NUMBER }}"
          git push

      - name: Send Slack Message
        run: |
          curl -X POST -H 'Content-type: application/json' \
          --data '{"text":"Deployment and commit completed :github_check_mark: :party_blob: - https://kedarnath-api-env.eba-9pwqzaur.us-east-1.elasticbeanstalk.com/"}' \
          ${{ secrets.SLACK_WEBHOOK_URL }}

      - name: Send Slack Message
        if: always()
        run: |
          curl -X POST -H 'Content-type: application/json' \
          --data '{"text":"Job status ${{ job.status }} "} ' \
          ${{ secrets.SLACK_WEBHOOK_URL }}

```

### step 395:

upload the aws file on elk and deploy

it wont fetch any data from backend
due to the security group

### step 396:

go to intsances running open the instance select the security tab and check the inbound rules
go to rds and check the inbound rules- it will allow 5432, so we should also add for 8080 to make it work

### step 397:

go to security groups on aws website
select the security group and click on edit inbound rules

click on add rule
type: Custom tcp
Port range: 8080
Source: custom
0.0.0.0/0
Description: spring boot api

save
deployed successfully

### step 398

in the future we might use managed solution to do this like we might put the frontend in one instance
and backend in another instance so we have to careful.
one more tip always deploy the backend first

### step 399

next step is to write workflow to automate the git hub actions

### step 400

create a new workflow:

frontend-react-cd.yml

```yaml
name: CD - Deploy Frontend React

on:
  workflow_dispatch:
  push:
    branches:
      - main
    paths:
      - frontend/react/**

jobs:
  deploy:
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: ./frontend/react

    steps:
      - uses: actions/checkout@v4

      - name: Slack commit message and sha
        run: |
          curl -X POST -H 'Content-type: application/json' \
          --data '{"text":"https://github.com/kedarnathyadav/spring-boot-fullstack/commit/${{ github.sha }} - ${{ github.event.head_commit.message }}"}' \
          ${{secrets.SLACK_WEBHOOK_URL}}

      - name: List Files in Working Directory
        run: ls -la

      - name: Send Slack Message
        run: |
          curl -X POST -H 'Content-type: application/json' \
          --data '{"text":"Deployment started :progress_bar: :spring:"}' \
          ${{ secrets.SLACK_WEBHOOK_URL }}

      - name: Set Build Number
        id: build-number
        run: echo "BUILD_NUMBER=$(date '+%d.%m.%Y.%H.%M.%S')" >> $GITHUB_OUTPUT

      - name: Login to Docker Hub
        uses: docker/login-action@v2
        with:
          username: ${{ secrets.DOCKERHUB_USERNAME }}
          password: ${{ secrets.DOCKERHUB_ACCESS_TOKEN }}

      - name: Docker Build and Push
        run: |
          chmod +x ../../.ci/build-publish.sh
          USERNAME=dkedarnath \
          REPO=kedarnath-react \
          TAG=${{ steps.build-number.outputs.BUILD_NUMBER }} \
          ../../.ci/build-publish.sh . \
           --build-arg api_base_url=<paste the elk url>:8080

      - name: Send Slack Message
        run: |
          curl -X POST -H 'Content-type: application/json' \
          --data '{"text":":docker: Image tag:${{ steps.build-number.outputs.BUILD_NUMBER }} pushed to https://hub.docker.com/repository/docker/dkedarnath/kedarnath-react"}' \
          ${{ secrets.SLACK_WEBHOOK_URL }}    

      - name: Update Dockerrun.aws.json api image tag with new build number
        run: |
          echo "Dockerrun.aws.json before updating the tag"
          cat ../../Dockerrun.aws.json
          sed -i -E 's|(dkedarnath/kedarnath-react:)([^"]*)_\1'${{ steps.build-number.outputs.BUILD_NUMBER }}'|' ../../Dockerrun.aws.json
          echo "Dockerrun.aws.json after updating the tag"
          cat ../../Dockerrun.aws.json

      - name: Send Slack Message
        run: |
          curl -X POST -H 'Content-type: application/json' \
          --data '{"text":":aws: Starting deployment to Elastic Beanstalk "}' \
          ${{ secrets.SLACK_WEBHOOK_URL }}

      - name: Deploy to EB
        uses: einaregilsson/beanstalk-deploy@v22
        with:
          aws_access_key: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws_secret_key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          application_name: ${{ secrets.EB_APPLICATION_NAME }}
          environment_name: ${{ secrets.EB_ENVIRONMENT_NAME }}
          version_label: ${{ steps.build-number.outputs.BUILD_NUMBER }}
          version_description: ${{ github.SHA }}
          region: ${{ secrets.EB_REGION }}
          deployment_package: Dockerrun.aws.json
          wait_for_environmnt_recovery: 60

      - name: Send Slack Message
        run: |
          curl -X POST -H 'Content-type: application/json' \
          --data '{"text":":githubloading: Committing to repo  "}' \
          ${{ secrets.SLACK_WEBHOOK_URL }}

      - name: Commit and Push Dockerrun.aws.json
        run: |
          git config user.name github-actions
          git config user.email github-actions@github.com
          git add ../../Dockerrun.aws.json
          git commit -m "Update Dockerrun.aws.json docker image with new tag ${{ steps.build-number.outputs.BUILD_NUMBER }}"
          git push

      - name: Send Slack Message
        run: |
          curl -X POST -H 'Content-type: application/json' \
          --data '{"text":"Deployment and commit completed :github_check_mark: :party_blob: - https://kedarnath-api-env.eba-9pwqzaur.us-east-1.elasticbeanstalk.com/"}' \
          ${{ secrets.SLACK_WEBHOOK_URL }}

      - name: Send Slack Message
        if: always()
        run: |
          curl -X POST -H 'Content-type: application/json' \
          --data '{"text":"Job status ${{ job.status }} "} ' \
          ${{ secrets.SLACK_WEBHOOK_URL }}

```

finished automating the deployment

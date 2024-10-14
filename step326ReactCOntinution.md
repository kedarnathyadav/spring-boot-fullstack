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

delete the gitignore file in the react project folder and paste the code into the main gitignore file

### step 328

open src folder play by changing the files

### step 329

ctrl + c to stop the app
npm run dev - to run the app
npm run build to build production ready dist folder

### step 330

delete App.css
update App.jsx

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

App.jsx

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

update App.jsx

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

App.jsx:

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
App.jsx

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

App.jsx:

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
import App from './App.jsx'

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

update the App.jsx:

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

update App.jsx

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
create a file Card.jsx

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

update App.jsx:

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

update App.jsx:

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

update App.jsx:

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

### step 358

### step 359

### step 360

### step 361

### step 362


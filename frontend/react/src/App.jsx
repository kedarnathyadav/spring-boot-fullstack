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

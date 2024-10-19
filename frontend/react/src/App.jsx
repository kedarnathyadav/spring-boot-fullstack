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

    const [customers, setCustomers] = useState([]); // Initialize as an array
    const [loading, setLoading] = useState(false);
    const [err, setError] = useState("");

    const fetchCustomers = () => {
        setLoading(true);
        getCustomers()
            .then(res => {
                console.log('API Response:', res.data);  // Debugging the response

                // Ensure the response is an array, or default to an empty array
                if (Array.isArray(res.data)) {
                    setCustomers(res.data);  // Set customers only if it's an array
                } else {
                    console.error("Unexpected response format:", res.data);
                    setCustomers([]);  // Fallback to an empty array if response is invalid
                    setError("Unexpected data format from API");
                }
            })
            .catch((err) => {
                console.error("Error fetching customers:", err);  // Log error for debugging
                setError(err?.response?.data?.message || "An error occurred while fetching customers");
                errorNotification(
                    err.code,
                    err?.response?.data?.message || "An error occurred"
                );
            })
            .finally(() => {
                setLoading(false);
            });
    };

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
                <CreateCustomerDrawer fetchCustomers={fetchCustomers}/>
                <Text mt={5}>Oops, there was an error: {err}</Text>
            </SidebarWithHeader>
        );
    }

    if (!Array.isArray(customers) || customers.length === 0) {
        return (
            <SidebarWithHeader>
                <CreateCustomerDrawer fetchCustomers={fetchCustomers}/>
                <Text mt={5}>No customers available.</Text>
            </SidebarWithHeader>
        );
    }

    return (
        <SidebarWithHeader>
            <CreateCustomerDrawer fetchCustomers={fetchCustomers}/>
            <Wrap justify="center" spacing="30px">
                {customers.map((customer, index) => (
                    <WrapItem key={index}>
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
};

export default App;

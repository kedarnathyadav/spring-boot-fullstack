import {
    Text,
} from '@chakra-ui/react';
import SidebarWithHeader from "./components/shared/SideBar";

const Home = () => {

    return (
        <SidebarWithHeader>
            <Text fontSize={"6xl"}>DashBoard</Text>
        </SidebarWithHeader>
    );
};

export default Home;

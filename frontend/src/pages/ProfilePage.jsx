import Sidebar from "../components/common/sidebar/Sidebar";
import SidearItem from "../components/common/sidebar/SidebarItem";

export default function ProfilePage() {

    return (
    <div>
            <Sidebar>
                <SidearItem icon="bi-house" text="Home" href="/"  />
                <SidearItem icon="bi-person" text="Profile" href="/profile" active />
                <SidearItem icon="bi-gear" text="Settings" href="/settings" />
            </Sidebar>
        <h1>Profile Page</h1>
    </div>
    );
}
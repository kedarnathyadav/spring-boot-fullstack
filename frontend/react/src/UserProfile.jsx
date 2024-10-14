const UserProfile = ({name, age, gender, imageNumber, ...props}) => {

    gender = gender === "MALE".toLowerCase() ? "male" : "female";
    return (
        <div>
            <p>{name}</p>
            <p>{age}</p>
            <p>{gender}</p>
            <img src={`https://randomuser.me/api/portraits/${gender}/${imageNumber}.jpg`} alt="User portrait"/>
            {props.children}
        </div>
    );
};

export default UserProfile;

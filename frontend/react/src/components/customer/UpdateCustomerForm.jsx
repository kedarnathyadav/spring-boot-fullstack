import {Formik, Form, useField} from 'formik';
import * as Yup from 'yup';
import {Alert, AlertIcon, Box, Button, FormLabel, Input, Stack} from "@chakra-ui/react";
import {updateCustomer} from "../../services/client.js";
import {errorNotification, successNotification} from "../../services/notification.js";

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
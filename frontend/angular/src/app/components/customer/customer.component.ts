import {Component, OnInit} from '@angular/core';
import {CustomerDTO} from "../../models/customer-dto";
import {CustomerServiceService} from "../../services/customer/customer-service.service";
import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
import {ConfirmationService, MessageService} from "primeng/api";

@Component({
  selector: 'app-customer',
  templateUrl: './customer.component.html',
  styleUrl: './customer.component.scss'
})
export class CustomerComponent implements OnInit {
  display: boolean = false;

  operation: 'create' | 'update' = 'create';


  customers: CustomerDTO[] = [];
  customer: CustomerRegistrationRequest = {};

  constructor(
    private customerService: CustomerServiceService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
  ) {
  }

  ngOnInit(): void {
    this.findAllCustomers();
  }

  private findAllCustomers() {
    this.customerService.findAll()
      .subscribe(
        {
          next: (data: CustomerDTO[]) => {
            this.customers = data;
            console.log(this.customers);
          }
        }
      )
  }

  save(customer: CustomerRegistrationRequest) {

    if (customer) {
      if (this.operation === "create") {
        this.customerService.registerCustomer(customer)
          .subscribe(
            {
              next: () => {
                this.display = false;
                this.findAllCustomers();
                this.customer = {};
                this.messageService.add(
                  {
                    severity: 'success',
                    summary: 'Success',
                    detail: `Customer ${customer.name} has been successfully saved.`,
                  });
              }
            });
      } else if (this.operation === "update") {
        this.customerService.updateCustomer(customer.id, customer)
          .subscribe(
            {
              next: () => {
                this.display = false;
                this.findAllCustomers();
                this.customer = {};
                this.messageService.add(
                  {
                    severity: 'success',
                    summary: 'Customer Updated',
                    detail: `Customer ${customer.name} has been successfully updated.`,
                  });
              }
            });
      }

    }
  }

  deleteCustomer(customer: CustomerDTO) {
    this.confirmationService.confirm(
      {
        header: 'Delete Customer',
        message: `Are you sure to delete ${customer.name}?`,
        accept: () => {
          this.customerService.deleteCustomer(customer.id)
            .subscribe(
              {
                next: () => {
                  this.findAllCustomers();
                  this.messageService.add(
                    {
                      severity: 'success',
                      summary: 'Customer deleted',
                      detail: `Customer ${customer.name} has been successfully deleted.`,
                    }
                  )
                }
              }
            )
        }
      }
    )
  }

  updateCustomer(customerDTO: CustomerDTO) {
    this.display = true;
    this.customer = customerDTO;
    this.operation = 'update';
  }

  createCustomer() {
    this.display = true;
    this.customer = {};
    this.operation = 'create';
  }

  cancel() {
    this.display = false;
    this.customer = {};
    this.operation = 'create';
  }
}

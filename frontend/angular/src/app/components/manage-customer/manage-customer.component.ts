import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CustomerRegistrationRequest} from "../../models/customer-registration-request";


@Component({
  selector: 'app-manage-customer',
  templateUrl: './manage-customer.component.html',
  styleUrl: './manage-customer.component.scss'
})
export class ManageCustomerComponent {

  @Input()
  customer: CustomerRegistrationRequest = {};
  @Input()
  operation: 'create' | 'update' = 'create';

  @Output()
  submit: EventEmitter<CustomerRegistrationRequest> = new EventEmitter<CustomerRegistrationRequest>;

  @Output()
  cancel: EventEmitter<void> = new EventEmitter<void>();

  get isCustomerValid(): boolean {
    return this.hasLength(this.customer.name) &&
      this.customer.age !== undefined && this.customer.age > 0 &&
      this.hasLength(this.customer.email) &&

      (this.operation === 'update' ||
        this.hasLength(this.customer.password) &&
        this.hasLength(this.customer.gender));
  }

  private hasLength(input: string | undefined): boolean {
    return input !== null && input !== undefined && input.length > 3;

  }

  onSubmit() {
    this.submit.emit(this.customer);
  }


  protected readonly oncancel = oncancel;

  onCancel() {
    this.cancel.emit();
  }
}

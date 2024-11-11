import {Component} from '@angular/core';
import {MenuItem} from "primeng/api";

@Component({
  selector: 'app-menu-bar',
  templateUrl: './menu-bar.component.html',
  styleUrl: './menu-bar.component.scss',
})
// export class MenuBarComponent implements OnInit {
//
//
//   items: MenuItem[] | undefined;
//
//   ngOnInit() {
//     this.items = [
//       {label: 'Home', icon: 'pi pi-home'},
//       {label: 'Customers', icon: 'pi pi-user'},
//       {label: 'Settings', icon: 'pi pi-cog'}
//     ];
//   }

export class MenuBarComponent {


  menu: Array<MenuItem> = [
    {label: 'Home', icon: 'pi pi-home'},
    {label: 'Customers', icon: 'pi pi-user'},
    {label: 'Settings', icon: 'pi pi-cog'}
  ];


}

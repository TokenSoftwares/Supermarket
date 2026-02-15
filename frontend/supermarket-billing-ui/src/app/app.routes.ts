import { Routes } from '@angular/router';
import { BillingComponent } from './pages/billing/billing';
import { BillDetails } from './pages/bill-details/bill-details';

export const routes: Routes = [
  { path: '', redirectTo: 'billing', pathMatch: 'full' },
  { path: 'billing', component: BillingComponent },
  { path: 'invoices/:id', component: BillDetails },
  { path: '**', redirectTo: 'billing' },
  { path: 'bills/:id', component: BillDetails },
];

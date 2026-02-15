import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs/operators';

import { ProductsService } from '../../core/services/products.service';
import { CartService, CartItem } from '../../core/services/cart.service';
import { BillsService } from '../../core/services/bills.service';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-billing',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatTableModule,
    MatIconModule,
    MatSelectModule,
    MatSnackBarModule,
  ],
  templateUrl: './billing.html',
  styleUrls: ['./billing.scss'],
})
export class BillingComponent implements AfterViewInit {
  @ViewChild('barcodeInput') barcodeInput!: ElementRef<HTMLInputElement>;

  barcode = '';
  qty = 1;
  paymentMethod: 'CASH' | 'CARD' = 'CASH';

  loading = false;
  checkoutLoading = false;

  displayedColumns = ['name', 'unitPrice', 'qty', 'discount', 'lineTotal', 'actions'];

  constructor(
    public cart: CartService,
    private products: ProductsService,
    private bills: BillsService,
    private snack: MatSnackBar,
  ) {}

  ngAfterViewInit(): void {
    this.focusBarcode();
  }

  focusBarcode() {
    setTimeout(() => this.barcodeInput?.nativeElement?.focus(), 0);
  }

  addByBarcode() {
    const code = this.barcode.trim();
    if (!code) return;

    this.loading = true;
    this.products
      .getByBarcode(code)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (p) => {
          this.cart.add(p, this.qty);
          this.barcode = '';
          this.qty = 1;
          this.focusBarcode();
        },
        error: (err) => {
          const msg =
            err?.status === 404
              ? 'Product not found'
              : err?.error?.message || 'Failed to fetch product';
          this.snack.open(msg, 'OK', { duration: 2500 });
          this.focusBarcode();
        },
      });
  }

  setDiscount(item: CartItem, value: any) {
    const d = Math.max(0, Number(value) || 0);
    this.cart.setDiscount(item.product.id, d);
  }

  lineTotal(item: CartItem) {
    // discount interpreted as absolute per-line discount (change if your backend expects %)
    const gross = item.product.price * item.quantity;
    return Math.max(0, gross - (item.discount || 0));
  }

  checkout() {
    if (this.cart.items.length === 0) {
      this.snack.open('Cart is empty', 'OK', { duration: 2000 });
      return;
    }

    const payload = {
      paymentMethod: this.paymentMethod,
      items: this.cart.toPayloadItems(),
    };

    this.checkoutLoading = true;
    this.bills
      .create(payload)
      .pipe(finalize(() => (this.checkoutLoading = false)))
      .subscribe({
        next: (bill) => {
          this.snack.open(`Bill created ✅`, 'OK', { duration: 2000 });
          this.cart.clear();
          this.focusBarcode();
          // later: navigate to bill details page
        },
        error: (err) => {
          this.snack.open(err?.error?.message || 'Checkout failed', 'OK', { duration: 3000 });
        },
      });
  }
}

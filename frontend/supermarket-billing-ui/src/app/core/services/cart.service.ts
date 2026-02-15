import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Product } from './products.service';

export interface CartItem {
  product: Product;
  quantity: number;
  discount: number;
}

@Injectable({ providedIn: 'root' })
export class CartService {
  private itemsSubject = new BehaviorSubject<CartItem[]>([]);
  items$ = this.itemsSubject.asObservable();

  get items() {
    return this.itemsSubject.value;
  }

  add(product: Product, qty = 1) {
    const q = Math.max(1, Math.floor(qty || 1));
    const items = [...this.items];
    const idx = items.findIndex((i) => i.product.id === product.id);

    if (idx >= 0) items[idx] = { ...items[idx], quantity: items[idx].quantity + q };
    else items.push({ product, quantity: q, discount: 0 });

    this.itemsSubject.next(items);
  }

  inc(id: number) {
    this.itemsSubject.next(
      this.items.map((i) => (i.product.id === id ? { ...i, quantity: i.quantity + 1 } : i)),
    );
  }

  dec(id: number) {
    this.itemsSubject.next(
      this.items
        .map((i) => (i.product.id === id ? { ...i, quantity: i.quantity - 1 } : i))
        .filter((i) => i.quantity > 0),
    );
  }

  remove(id: number) {
    this.itemsSubject.next(this.items.filter((i) => i.product.id !== id));
  }

  clear() {
    this.itemsSubject.next([]);
  }

  subtotal() {
    return this.items.reduce((s, i) => {
      const gross = i.product.price * i.quantity;
      const net = Math.max(0, gross - (i.discount ?? 0));
      return s + net;
    }, 0);
  }

  setDiscount(productId: number, discount: number) {
    const d = Math.max(0, Number(discount) || 0);

    this.itemsSubject.next(
      this.items.map((i) => (i.product.id === productId ? { ...i, discount: d } : i)),
    );
  }
  toPayloadItems() {
    return this.items.map((i) => ({
      productId: i.product.id,
      quantity: i.quantity,
      discount: i.discount ?? 0,
    }));
  }
}

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export interface Product {
  id: number;
  barcode: string;
  name: string;
  price: number;
  stockQty?: number;
}

@Injectable({ providedIn: 'root' })
export class ProductsService {
  private base = `${environment.apiBaseUrl}/products`;

  constructor(private http: HttpClient) {}

  getByBarcode(barcode: string) {
    return this.http.get<Product>(`${this.base}/barcode/${encodeURIComponent(barcode)}`);
  }
}

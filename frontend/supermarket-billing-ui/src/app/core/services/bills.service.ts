import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export type PaymentMethod = 'CASH' | 'CARD'; // add others if you have

export interface CreateBillItemRequest {
  productId: number;
  quantity: number;
  discount: number;
}

export interface CreateBillRequest {
  paymentMethod: PaymentMethod;
  items: CreateBillItemRequest[];
}

@Injectable({ providedIn: 'root' })
export class BillsService {
  private base = `${environment.apiBaseUrl}/bills`;

  constructor(private http: HttpClient) {}

  create(payload: CreateBillRequest) {
    return this.http.post<any>(this.base, payload);
  }

  getById(id: number) {
    return this.http.get<any>(`${this.base}/${id}`);
  }
}

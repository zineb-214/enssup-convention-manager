import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Page } from './user-service.service';



export interface AppConventionType {
  id?: number;
  name: string;
  description: string;
  code:string
}

@Injectable({
  providedIn: 'root'
})
export class ConventionTypeService {
  private baseUrl = 'http://localhost:8080/admin';
  constructor(private http:HttpClient) { }

  getConventionTypes(): Observable<AppConventionType[]> {
      return this.http.get<AppConventionType[]>(`${this.baseUrl}/types`);
    }

    addConventionType(type: AppConventionType): Observable<AppConventionType> {
        return this.http.post<AppConventionType>(`${this.baseUrl}/addType`, type);
      }
        updateConventionType(id: number, type: AppConventionType): Observable<AppConventionType> {
          return this.http.put<AppConventionType>(`${this.baseUrl}/updateType/${id}`, type);
        }
      deleteConventionType(id: number): Observable<string> {
  return this.http.delete(`${this.baseUrl}/deleteType/${id}`, {
    responseType: 'text' as const 
  });
}
filterTypes(name?: string, code?: string, page: number = 0, size: number = 10): Observable<Page<AppConventionType>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (name) {
      params = params.set('name', name);
    }

    if (code) {
      params = params.set('code', code);
    }

    return this.http.get<Page<AppConventionType>>(`${this.baseUrl}/search`, { params });
  }
}
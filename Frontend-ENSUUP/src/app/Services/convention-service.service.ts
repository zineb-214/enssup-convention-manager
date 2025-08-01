import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { AppConventionType } from './convention-type.service';

export interface UserDTO {
  id: number;
  username: string;
}

export interface ConventionTypeDTO {
  id: number;
  name: string;
}

export interface AppConvention {
  id: number;
  title: string;
  conventionNumber: string;
  object: string;
  signatureDate: string;
  startDate: string;
  endDate: string;
  partners: string;
  filePath: string;
  createdAt: string;

  createdBy?: UserDTO;
  conventionType?: ConventionTypeDTO;
  typeCode: string;

  // Champs ECHANGES
  natureEchange?: string;
  modaliteEchange?: string;
  logistique?: string;
  assuranceResponsabilite?: string;
  renouvellement?: string;
  resiliation?: string;

  // Champs ACCORD
  perimetre?: string;
  modalite?: string;

  // Champs dynamiques
  customFields?: { [key: string]: string };
}

export interface ConventionRequestDTO {
  typeId: number;
  title: string;
  conventionNumber: string;
  object: string;
  signatureDate: string;
  startDate: string;
  endDate: string;
  partners: string;
  filePath: string;

  // Champs spécifiques
  natureEchange?: string;
  modaliteEchange?: string;
  logistique?: string;
  assuranceResponsabilite?: string;
  renouvellement?: string;
  resiliation?: string;

  perimetre?: string;
  modalite?: string;

  customFields?: { [key: string]: string };
}


@Injectable({
  providedIn: 'root'
})
export class ConventionServiceService {
private baseUrl = 'http://localhost:8080/user';

  constructor(private http: HttpClient) {}
  createConvention(dto: any) {
  return this.http.post(`${this.baseUrl}/createConvention`, dto);
}
getConventionTypes(): Observable<AppConventionType[]> {
  return this.http.get<AppConventionType[]>(`${this.baseUrl}/public/types`);
}
getAllConventions(): Observable<AppConvention[]> {
  return this.http.get<AppConvention[]>(`${this.baseUrl}/conventions`);
}

filterConventions(filters: any): Observable<any> {
  const params: any = {};

  if (filters.code) params.code = filters.code;
  if (filters.title) params.title = filters.title;
  if (filters.startDateFrom) params.startDateFrom = filters.startDateFrom;
  if (filters.startDateTo) params.startDateTo = filters.startDateTo;

  return this.http.get<any>(`${this.baseUrl}/conventions/filter`, { params });
}
updateConvention(id: number, data: ConventionRequestDTO): Observable<any> {
  return this.http.put(`${this.baseUrl}/updateConvention/${id}`, data);
}
getConventionById(id: number): Observable<AppConvention> {
  return this.http.get<AppConvention>(`${this.baseUrl}/convention/${id}`);
}
deleteConvention(id: number): Observable<any> {
  return this.http.delete(`${this.baseUrl}/deleteConvention/${id}`, {
    responseType: 'text' as 'json'
  });
}



}

import { HttpClient, HttpEvent, HttpParams, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DocumentsService {
 private baseUrl = 'http://localhost:8080/files';
 private baseUrl2 = 'http://localhost:8080/api/documents';
 addDocument(conventionId: number, nom: string) {
  const params = new HttpParams()
    .set('conventionId', conventionId)
    .set('nom', nom);

  return this.http.post(`${this.baseUrl2}/add`, null, { params });
}

  constructor(private http: HttpClient) { }
   upload(file: File): Observable<HttpEvent<any>> {
    const formData: FormData = new FormData();
    formData.append('file', file);

    const req = new HttpRequest('POST',`${this.baseUrl}/upload`, formData, {
      reportProgress: true, 
      responseType: 'text'
    });

    return this.http.request(req);
  }
  

  downloadFile(fileName: string) {
    return this.http.get(`${this.baseUrl}/download/${encodeURIComponent(fileName)}`, {
      responseType: 'blob', 
      observe: 'response'
    });
  }
}

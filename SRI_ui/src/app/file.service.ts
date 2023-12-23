import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FileService {
   url = 'http://localhost:8080/api/v1/files';


  constructor(private http: HttpClient) { }

  public getFiles(): Observable<any> {
    return this.http.get<any>(this.url);
}
    searchFiles(find: string): Observable<any> {
      const searchData = {
        find: find
      };

      const headers = new HttpHeaders({
        'Content-Type': 'application/json'
      });

      return this.http.post<any>("http://localhost:8080/api/v1/search/lucene", searchData, { headers: headers });
    }

}

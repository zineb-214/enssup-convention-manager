import { Component } from '@angular/core';
import { DocumentsService } from '../../../Services/documents.service';

@Component({
  selector: 'app-user-home',
  standalone: false,
  templateUrl: './user-home.component.html',
  styleUrl: './user-home.component.css'
})
export class UserHomeComponent {


  constructor(private fileService: DocumentsService) {}

  
}

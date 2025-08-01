import { Component, OnInit } from '@angular/core';
import { AppConventionType, ConventionTypeService } from '../../../Services/convention-type.service';

@Component({
  selector: 'app-convention-type',
  standalone: false,
  templateUrl: './convention-type.component.html',
  styleUrl: './convention-type.component.css'
})
export class ConventionTypeComponent implements OnInit {
  conventionTypes: AppConventionType[] = [];
  newConventionType: AppConventionType = { name: '', description: '', code:''};
  editMode: boolean = false;
  currentEditId?: number;
  message: string = '';
  showAddForm: boolean = false;
  selectedConventionId: number | null = null;
  nameFilter: string = '';
codeFilter: string = '';

  constructor(private conventionTypeService: ConventionTypeService) {}
ngOnInit(): void {
    this.loadConventionTypes();
}
loadConventionTypes(): void {
  this.conventionTypeService
    .filterTypes(this.nameFilter, this.codeFilter)
    .subscribe((page) => {
      this.conventionTypes = page.content;
    });
}

  addConventionType(): void {
    if (!this.newConventionType.name || !this.newConventionType.description) return;

    this.conventionTypeService.addConventionType(this.newConventionType).subscribe(result => {
      this.message = "Convention ajoutée.";
      this.loadConventionTypes();
      this.newConventionType = { name: '', description: '' , code:''};
    });
    this.showAddForm=false;
  }

  editConventionType(type: AppConventionType): void {
    this.editMode = true;
    this.currentEditId = type.id;
    this.newConventionType = { ...type };
  }

  updateConventionType(): void {
    if (this.currentEditId == null) return;

    this.conventionTypeService.updateConventionType(this.currentEditId, this.newConventionType).subscribe(result => {
      this.message = "Convention modifiée.";
      this.loadConventionTypes();
      this.cancelEdit();
    });
  }

  deleteConventionType(id: number): void {
  if (confirm("Voulez-vous vraiment supprimer ce type ?")) {
    this.conventionTypeService.deleteConventionType(id).subscribe({
      next: (result) => {
        this.message = result;
        this.loadConventionTypes();
      },
      error: (err) => {
        console.error("Erreur lors de la suppression :", err);
        alert("Erreur serveur lors de la suppression. Voir console pour détails.");
      }
    });
  }
}


  cancelEdit(): void {
    this.editMode = false;
    this.currentEditId = undefined;
    this.newConventionType = { name: '', description: '',code:'' };
  }
  toggleDetails(id: number) {
  this.selectedConventionId = this.selectedConventionId === id ? null : id;
}
onFilter(): void {
  this.loadConventionTypes();
}
}

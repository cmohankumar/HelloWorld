import { Component } from '@angular/core';

@Component({
  selector: 'app-parent',
  templateUrl: './parent.component.html',
  styleUrls: ['./parent.component.css']
})
export class ParentComponent {
  showChild = true;
  savedChildData: {
    field1: string;
    field2: string;
    field3: string;
    arrayField: string[];
  } = {
    field1: '',
    field2: '',
    field3: '',
    arrayField: []
  };

  toggleChild() {
    if (this.showChild) {
      // Child component will call this method before being hidden
      this.requestChildData();
    }
    this.showChild = !this.showChild;
  }

  saveChildData(data: any) {
    this.savedChildData = { ...data };
    console.log('Saved child data:', this.savedChildData);
  }

  requestChildData() {
    // This method will be overwritten by the child component
  }
}
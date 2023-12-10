import { Component ,ViewEncapsulation } from '@angular/core';
import { FileService } from '../file.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css'],
  encapsulation: ViewEncapsulation.None,

})
export class SearchComponent {


  docs = new Array<any>();

  constructor(public fileService: FileService) { }

  ngOnInit(): void {
   this.getFileData();
  }
  getFileData() {
    this.fileService.getFiles().subscribe(
      (data) => {
        // Handle the file data here
        this.docs=data
        console.log('Files:', data);
      },
      (error) => {
        console.error('Error fetching files:', error);
      }
    );
  }

  //When we click the search button this value turns to true and the filters get displayed
  clicked = false ;
// docs = ['document 1','document 2', 'document 3','document 4','document 5','document 6','document 7','document 7','document 7','document 7','document 7']
 config = {
    id: 'basicPaginate',
    itemsPerPage: 6,
    currentPage: 1,
    totalItems: this.docs.length
  };
 totalPages = Math.ceil(this.docs.length / this.config.itemsPerPage);

  // Generate a list of page numbers
   numbers = Array.from({ length: this.totalPages }, (_, index) => index + 1);
  
  pageChanged(event :any ){
    this.config.currentPage = event ;
  }

  onSearch() {
   
    this.clicked = true ;
    console.log('button',this.clicked)

  }

  listCV = [{
    skills : 'Java , C++,Talend',
    experience :'Bac+5',
    emploi : 'stage'
  },
  {
    skills : 'Python , C,Talend , Docker',
    experience :'Bac+5',
    emploi : 'premier emploi'
  }]

 

}

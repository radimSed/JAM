"use strict";

import { pathToServer, Company, CompanyCard, Person, PersonCard, MD, AbstractOperations } from "./global.js";

class CompanyCardSel extends CompanyCard{
    constructor(id, name, card, dialog){
        super(id, name, card, dialog);
    }

    render(whereTo){
        const cardDiv = document.createElement("div");
        cardDiv.classList.add("cardDiv");
        cardDiv.innerHTML = this._card;
        cardDiv.addEventListener("click", () => {
            this._dialog.hide();//hide company selection modal dialog
            this._dialog.getAppModalDialogRef().setCompany(this._id, this._name);
        });
        whereTo.appendChild(cardDiv);
    }
}

class PersonCardSel extends PersonCard{
    constructor(id, name, email, phone, card, dialog){
        super(id, name, email, phone, card, dialog);
    }

    render(whereTo){
        const cardDiv = document.createElement("div");
        cardDiv.classList.add("cardDiv");
        cardDiv.innerHTML = this._card;
        cardDiv.addEventListener("click", () => {
            this._dialog.hide();//hide person selection modal dialog
            this._dialog.getAppModalDialogRef().setPerson(this._id, this._name, this._email, this._phone);
        });
        whereTo.appendChild(cardDiv);
    }
}

/**
 * class for creation
 */
class Application{
    _id;
    _title;
    _positionId;
    _startDate;
    _status;
    _companyId;
    _personId;

    constructor(id, title, positionId, startDate, status, companyId, personId){
        this._id = id;
        this._title = title;
        this._positionId = positionId;
        this._startDate = startDate;
        this._status = status;
        this._companyId = companyId;
        this._personId = personId;
    }

    getId(){
        return this._id;
    }

    getTitle(){
        return this._title;
    }

    getPositionId(){
        return this._positionId;
    }

    getStartDate(){
        return this._startDate;
    }

    getStatus(){
        return this._status;
    }

    getCompanyId(){
        return this._companyId;
    }

    getPersonId(){
        return this._personId;
    }
}

/**
* class for modification including adding record
  */
class ApplicationUpdate extends Application{
    //_record;
    _newRecord;

    constructor(id, title, positionId, startDate, status, companyId, personId, newRecord){
        super(id, title, positionId, startDate, status, companyId, personId);
        //this._record = record;
        this._newRecord = newRecord;
    }

    getRecord(){
        return this._record;
    }

    getNewRecord(){
        return this._newRecord;

    }
}

/**
 * class holding detail of the application
 */
class AppDetail{
    _id;
    _title;
    _positionId;
    _startDate;
    _lastInteractionDate;
    _status;
    _records;
    _company;
    _person;

    constructor(appId, posTitle, posId, startDate, lastDate, status,
        records, company, person){

        this._id = appId;
        this._title = posTitle, 
        this._positionId = posId;
        this._startDate = startDate;
        this._lastInteractionDate = lastDate;
        this._status = status;
        this._records = records;
        this._company = company;
        this._person = person;
    }

    getId(){
        return this._id;
    }

    getTitle() {
        return this._title;
    }

    getPositionId(){
        return this._positionId;
    }

    getStartDate(){
        return this._startDate;
    }

    getLastInteractionDate(){
        return this._lastInteractionDate;
    }

    getStatus(){
        return this._status;
    }

    getRecords(){
        return this._records;
    }

    getCompany(){
        return this._company;
    }

    getPerson(){
        return this._person;
    }
}

class CompSelModalDialog{
    _compSelModalDialog; //div in html
    _appModalDialog; //application create/update/delete modal dialog
    _idInput; 
    _nameInput;
    _listBtn;
    _closeSpan;
    _info;
    _cardContainer;
    _pathToServer;
    _resultField = [];

    constructor(compSelection, appModalDialogObject, idInput, nameInput, listBtn, closeSpan, compInfo, compCardContainer, pathToServer){
        this._compSelModalDialog = compSelection;
        this._appModalDialog = appModalDialogObject;
        this._idInput = idInput;
        this._nameInput = nameInput;
        this._listBtn = listBtn;
        this._closeSpan = closeSpan;
        this._info = compInfo;
        this._cardContainer = compCardContainer;
        this._pathToServer = pathToServer

        this.addListeners();
    }

    addListeners(){
        this._listBtn.addEventListener('click', () => {
            this.getList();
        });

        this._closeSpan.addEventListener("click", () => {
            this.hide();
        });

    }
    
    hide() {
        this._compSelModalDialog.style.display = "none";
    }
        
    display() {
        this._compSelModalDialog.style.display = "block";
    }

    getAppModalDialogRef(){
        return this._appModalDialog;
    }

    /**
    * gets list of companies according to filters
    */
    async getList(){
        let idValue = this._idInput.value;
        const nameValue = this._nameInput.value;
        if (idValue.length === 0 || parseInt(idValue) < 0) {
            idValue = "0";
        }
        let query = `${pathToServer}comp?id=${idValue}&name=${nameValue}`;
        const data = await fetch(query).then((response) => response.json());
    
        this._resultField = []; //clear array of previous results
        data.forEach((portion) => { //create card for each company obtained
            let compCard = new CompanyCardSel(portion.id, portion.name, portion.card, this);
            this._resultField.push(compCard);
        });

        this._info.textContent = ""; //clear previous info and show new one
        const info = `Number of retrieved results: ${this._resultField.length}`;
        this._info.innerText = info;
        this._cardContainer.textContent = ""; //clear previous results and show new one
        this._resultField.forEach((card) => {
            card.render(this._cardContainer);
        });
    };
}

class ModalRecord{
    _modalDialog; //div in html
    _appModalDialog; //application create/update/delete modal dialog
    _closeSpan;
    _textArea;
    _modalRecBtn;

    constructor(modalDialog, appModalDialog, closeSpan, textArea, modalRecBtn){
        this._modalDialog = modalDialog;
        this._appModalDialog = appModalDialog;
        this._closeSpan = closeSpan;
        this._textArea = textArea;
        this._modalRecBtn = modalRecBtn;

        this.addListeners();
    }

    addListeners(){
        this._closeSpan.addEventListener("click", () => {
            this.hide();
        });

        this._modalRecBtn.addEventListener('click', () => {
            this._appModalDialog.updateApplication();
            this._textArea.value = '';
            this.hide();
        })
    }

    display(){
        this._modalDialog.style.display = 'block';
    }

    hide(){
        this._modalDialog.style.display = 'none';
    }

    getRecord(){
        return this._textArea.value;
    }
}

class PersonSelModalDialog extends CompSelModalDialog{
    _emailInput;
    _phoneInput;

    constructor(compSelection, appModalDialogObject, idInput, nameInput, emailInput, phoneInput, listBtn, closeSpan, compInfo, compCardContainer, pathToServer){
        super(compSelection, appModalDialogObject, idInput, nameInput, listBtn, closeSpan, compInfo, compCardContainer, pathToServer)
        this._emailInput = emailInput;
        this._phoneInput = phoneInput;
    }

    /**
    * gets list of persons according to filters
    */
    async getList(){
        let idValue = this._idInput.value;
        const nameValue = this._nameInput.value;
        const emailValue = this._emailInput.value;
        const phoneValue = this._phoneInput.value;
        if (idValue.length === 0 || parseInt(idValue) < 0) {
            idValue = "0";
        }
        let query = `${pathToServer}person?id=${idValue}&name=${nameValue}&email=${emailValue}&phone=${phoneValue}`;
        const data = await fetch(query).then((response) => response.json());
    
        this._resultField = []; //clear array of previous results
        data.forEach((portion) => { //create card for each company obtained
            let persCard = new PersonCardSel(portion.id, portion.name, portion.email, portion.phone, portion.card, this);
            this._resultField.push(persCard);
        });

        this._info.textContent = ""; //clear previous info and show new one
        const info = `Number of retrieved results: ${this._resultField.length}`;
        this._info.innerText = info;
        this._cardContainer.textContent = ""; //clear previous results and show new one
        this._resultField.forEach((card) => {
            card.render(this._cardContainer);
        });
    };
}


class ModalDialog extends MD{
    _deleteBtn;
    _createBtn;
    _editBtn;
    _companyEditBtn;
    _personEditBtn;
    _recordEditBtn;
    _modalHorLine1;
    _dialog;
    _span;
    _appIdInput;
    _appTitleInput;
    _appPosIdInput;
    _appStartDateInput;
    _appLastDateInput;
    _statusInput;
    _appPersonIdInput;
    _appPersonNameInput;
    _appPersonEmailInput;
    _appPersonPhoneInput;
    _appCompanyIdInput;
    _appCompanyNameInput;
    _appRecordsTA;
    _companySelection;
    _personSelection;
    _appNewRecordMD;
    _operations;
    
    constructor(deleteBtn, createBtn, EditBtn, companyEditBtn, personEditBtn, recordEditBtn, 
                modalHorLine1, dialog, span, idInput, titleInput, posIdInput, startDateInput, 
                lastDateInput, statusInput, personIdInput, personNameInput, personEmailInput, 
                personPhoneInput, companyIdInput, companyNameInput, recordsTA,
                operations){
        super();
        this._deleteBtn = deleteBtn;
        this._createBtn = createBtn;
        this._editBtn = EditBtn;
        this._companyEditBtn = companyEditBtn;
        this._personEditBtn = personEditBtn;
        this._recordEditBtn = recordEditBtn;
        this._modalHorLine1 = modalHorLine1;
        this._dialog = dialog;
        this._span = span;
        this._appIdInput = idInput;
        this._appTitleInput = titleInput;
        this._appPosIdInput = posIdInput;
        this._appStartDateInput = startDateInput;
        this._appLastDateInput = lastDateInput;
        this._statusInput = statusInput;
        this._appPersonIdInput = personIdInput;
        this._appPersonNameInput = personNameInput;
        this._appPersonEmailInput = personEmailInput;
        this._appPersonPhoneInput = personPhoneInput;
        this._appCompanyIdInput = companyIdInput;
        this._appCompanyNameInput = companyNameInput;
        this._appRecordsTA = recordsTA;
        this._companySelection = new CompSelModalDialog(document.getElementById("compSelection"),
                                 this,
                                 document.getElementById("idCompInput"),
                                 document.getElementById("compNnameInput"),
                                 document.getElementById("compListBtn"),
                                 document.getElementById("compSelClose"),
                                 document.getElementById("compInfo"),
                                 document.getElementById("compCardsContainer"));
        this._personSelection = new PersonSelModalDialog(document.getElementById("persSelection"),
                                                         this,
                                                         document.getElementById("persIdInput"),
                                                         document.getElementById("persNameInput"),
                                                         document.getElementById("emailInput"),
                                                         document.getElementById("phoneInput"),
                                                         document.getElementById("persListBtn"),
                                                         document.getElementById("persSelClose"),
                                                         document.getElementById("persInfo"),
                                                         document.getElementById("persCardsContainer"));

        this._appNewRecordMD = new ModalRecord(document.getElementById("modalRecord"),
                                               this,
                                               document.getElementById("modalRecClose"),
                                               document.getElementById("modalTextArea"),
                                               document.getElementById("modalRecBtn"));
        
        this._operations = operations;

        this.addListeners();
        this._operations.fillStatusSelection(this._statusInput);
    }

    addListeners() {
        this._span.addEventListener("click", () => {
            this.hide();
        });

        this._editBtn.addEventListener("click", () => {
            this.updateApplication();
        });
            
        this._deleteBtn.addEventListener("click", () => {
            const app = new Application(this._appIdInput.value, this._appTitleInput.value, this._appPosIdInput.value, this._appStartDateInput.value,
                this._statusInput.value, this._appCompanyIdInput.value, this._appPersonIdInput.value
            )
            this._operations.delete(app);
            this.hide();        
        });

        this._createBtn.addEventListener('click', () => {
            const app = new Application('', this._appTitleInput.value, this._appPosIdInput.value, this._appStartDateInput.value,
                this._statusInput.value, this._appCompanyIdInput.value, this._appPersonIdInput.value
            )
            this._operations.create(app);
            this.hide();
        });

        this._companyEditBtn.addEventListener('click', () => {
            this._companySelection.display();
        });

        this._personEditBtn.addEventListener('click', () => {
            this._personSelection.display();
        });

        this._recordEditBtn.addEventListener('click', () => {
            this._appNewRecordMD.display();
        });
    }

    updateApplication(){
        const app = new ApplicationUpdate(this._appIdInput.value, this._appTitleInput.value, 
            this._appPosIdInput.value, this._appStartDateInput.value, this._statusInput.value, 
            this._appCompanyIdInput.value, this._appPersonIdInput.value, this._appNewRecordMD.getRecord()
        )
        this._operations.update(app);
        this.hide();
    }


    hide() {
        this._dialog.style.display = "none";
    }
        
    display(object, createNew) {
        let detail = object;
        if (createNew === true) {
            this._deleteBtn.style.display = "none";
            this._editBtn.style.display = 'none';
            this._createBtn.style.display = 'block';
            this._recordEditBtn.style.display = 'none';
            this._appRecordsTA.style.display = 'none'
            this._modalHorLine1.style.display = 'none';
        } else {
            this._deleteBtn.style.display = "block";
            this._editBtn.style.display = 'block';
            this._createBtn.style.display = 'none';
            this._recordEditBtn.style.display = 'block';
            this._appRecordsTA.style.display = 'block'
            this._modalHorLine1.style.display = 'block';
        }
        if (detail !== null) {
            this._appIdInput.value = detail.getId();
            this._appTitleInput.value = detail.getTitle();
            this._appPosIdInput.value = detail.getPositionId();
            this._appStartDateInput.value = detail.getStartDate();
            this._appLastDateInput.value = detail.getLastInteractionDate();
            this._statusInput.value = detail.getStatus();
            this._appPersonIdInput.value = detail.getPerson().getId();
            this._appPersonNameInput.value = detail.getPerson().getName();
            this._appPersonEmailInput.value = detail.getPerson().getEmail();
            this._appPersonPhoneInput.value = detail.getPerson().getPhone();
            this._appCompanyIdInput.value = detail.getCompany().getId();
            this._appCompanyNameInput.value = detail.getCompany().getName();
            this._appRecordsTA.value = detail.getRecords();
        } else {
            this._appIdInput.value = '';
            this._appTitleInput.value = '';
            this._appPosIdInput.value = '';
            this._appStartDateInput.value = '';
            this._appLastDateInput.value = '';
            this._statusInput.value = '';
            this._appPersonIdInput.value = '';
            this._appPersonNameInput.value = '';
            this._appPersonEmailInput.value = '';
            this._appPersonPhoneInput.value = '';
            this._appCompanyIdInput.value = '';
            this._appCompanyNameInput.value = '';
            this._appRecordsTA.value = '';
        }
        this._dialog.style.display = "block";
    }

    setCompany(id, name){
        this._appCompanyIdInput.value = id;
        this._appCompanyNameInput.value = name;
    }

    setPerson(id, name, email, phone){
        this._appPersonIdInput.value = id;
        this._appPersonNameInput.value = name;
        this._appPersonEmailInput.value = name;
        this._appPersonPhoneInput.value = name;
    }

    getAppId(){
        return this._appIdInput.value;
    }

    getTitle(){
        return this._appTitleInput.value;
    }

    getPositionId(){
        return this._appPosIdInput.value;
    }

    getStatus(){
        return this._statusInput.value;
    }

    getPersoId(){
        return this._appPersonIdInput.value;
    }

    getCompanyId(){
        return this._appCompanyIdInput.value;
    }
}

class Operations extends AbstractOperations{
    
    constructor(pathToServer) {
        super(pathToServer);
    };
    
    async create(object) {
        try{
            this._checkInputs(object);

            let jsonData;
            jsonData = `{ "positionTitle":"${object.getTitle()}", "positionId":"${object.getPositionId()}",
                "personId":"${object.getPersonId()}", "companyId":"${object.getCompanyId()}" }`;
          
            const queryMethod = "POST";
            const responseData = await fetch(`${this._pathToServer}app`, {
                method: queryMethod,
                headers: {
                    Accept: "application/json",
                    "Content-Type": "application/json",
                },
                body: jsonData,
            });
            this.processResponse(responseData);    
        } catch (e) {
            alert(e.message);
        }
    };
    
    async update(object) {
            let jsonData;
            jsonData = `{ "applicationId":"${object.getId()}", "positionTitle":"${object.getTitle()}", 
                        "positionId":"${object.getPositionId()}", "personId":"${object.getPersonId()}", 
                        "companyId":"${object.getCompanyId()}", "statusId":"${object.getStatus()}", 
                "record":"${object.getNewRecord()}" }`;
                
            const queryMethod = "PUT";
            const responseData = await fetch(`${this._pathToServer}app`, {
                method: queryMethod,
                headers: {
                    Accept: "application/json",
                    "Content-Type": "application/json",
                },
                body: jsonData,
            });
            this.processResponse(responseData);    
    };

    async delete(object) {
            const queryMethod = "DELETE";
            const query = `${this._pathToServer}app/${object.getId()}`;
            const responseData = await fetch(query, {
                method: queryMethod,
            });
            this.processResponse(responseData);
    };

    _checkInputs(object){
        if(object.getPersonId() === ''){
            throw new Error('Select a contact person.');
        }
        if(object.getCompanyId() === ''){
            throw new Error('Select a company.');
        }
    }

    /**
     * reads detailed information about application
     * @param {*} id 
     * @returns 
     */
    async readDetail(id){
        let query = `${pathToServer}app/${id}`;
        const response = await fetch(query);
        const detail = await response.json();
        return detail;
    }

    /**
    * fills selection in filters with statuses of the application
    * @param {} path 
    * @param {*} selection 
    */
    async fillStatusSelection(selection){
        const query = `${this._pathToServer}status`;
        const response = await fetch(query);
        const data = await response.json();
        data.forEach((statusValue) => {
            let option = document.createElement("option");
            option.value = statusValue.id;
            option.textContent = statusValue.status;
            selection.add(option);
        });
    };

}

/**
* class for objects containing data of the received person
*/
class AppCard{
    _id;
    _title;
    _positionId;
    _companyName;
    _personName;
    _status;
    _card;
    _dialog;
    _operations;

    constructor(id, title, positionId, companyName, personName, status, card, modalDialog, operations) {
        this._id = id;
        this._title = title;
        this._positionId = positionId;
        this._companyName = companyName;
        this._personName = personName;
        this._status = status;
        this._card = card;
        this._dialog = modalDialog;
        this._operations = operations;
    }


        
    /**
    * shows cards of the received companies. On click --> show modal dialog for possible changes
    * @param whereTo
    */
    render(whereTo) {
        const cardDiv = document.createElement("div");
        cardDiv.classList.add("cardDiv");
        cardDiv.innerHTML = this._card;
        cardDiv.addEventListener("click", async() => {
            const data = await this._operations.readDetail(this._id);
            const appDetail = new AppDetail(data.applicationId, data.title, data.positionId, data.startDate,
                data.lastInteractionDate, data.status, data.records, 
                new Company(data.company.id, data.company.name),
                new Person(data.person.id, data.person.name, data.person.email, data.person.phone)
            )
            this._dialog.display(appDetail, false);
        });
        whereTo.appendChild(cardDiv);
    }
}


/*************************************************************************************/
/**
* global variables/constants
*/
//array with results
let resultField = [];
const idInput = document.getElementById("idInput");
const titleInput = document.getElementById("titleInput");
const posIdInput = document.getElementById("posIdInput");
const compNameInput = document.getElementById("compNameInput");
const persNameInput = document.getElementById("personNameInput");
const statusSelection = document.getElementById("statusSel");
const resultDiv = document.getElementById("results");
const infoDiv = document.getElementById("info");
const cardsContainer = document.getElementById("cardsContainer");
//read buttons
const listBtn = document.querySelector("#listBtn");
const createBtn = document.querySelector("#createBtn");

//initiate operation object
let operations = new Operations(pathToServer);

//initiate modal dialog object
let modalDialog = new ModalDialog(document.getElementById("modalDeleteBtn"),
                                  document.getElementById("modalCreateBtn"),
                                  document.getElementById("modalEditBtn"),
                                  document.getElementById('modalCompEditBtn'),
                                  document.getElementById('modalPersEditBtn'),
                                  document.getElementById('modalNoteEditBtn'),
                                  document.getElementById('modalHorizontalLine1'),
                                  document.getElementById("modalDialog"),
                                  document.getElementById("close"),
                                  document.getElementById('modalIdInput'),
                                  document.getElementById('modalTitleInput'),
                                  document.getElementById('modalPosIdInput'),
                                  document.getElementById('modalStartDateInput'),
                                  document.getElementById('modalLastDateInput'),
                                  document.getElementById('modalStatusInput'),
                                  document.getElementById('modalPersonIdInput'),
                                  document.getElementById('modalPersonNameInput'),
                                  document.getElementById('modalPersonEmailInput'),
                                  document.getElementById('modalPersonPhoneInput'),
                                  document.getElementById('modalCompanyIdInput'),
                                  document.getElementById('modalCompNameInput'),
                                  document.getElementById('textArea'),
                                  //document.getElementById('modalTextArea'),
                                  operations);

/**
* gets list of applications according to filters
*/
const getList = async () => {
    let idValue = idInput.value;
    const titleValue = titleInput.value;
    const posIdValue = posIdInput.value;
    const compNameValue = compNameInput.value;
    const persNameValue = persNameInput.value;
    const statusValue = statusSelection[statusSelection.selectedIndex].textContent;
    if (idValue.length === 0 || parseInt(idValue) < 0) {
        idValue = "0";
    }
    let query = `${pathToServer}preview?id=${idValue}&title=${titleValue}&posId=${posIdValue}&compName=${compNameValue}&persName=${persNameValue}&status=${statusValue}`;
    const data = await fetch(query).then((response) => response.json());
    //processResults(data);
    resultField = []; //clear array of previous results
    data.forEach((portion) => {//create card for each application obtained
        let appCard = new AppCard(portion.id, portion.title, portion.positionId, portion.companyName, portion.personName, portion.status, portion.card, modalDialog, operations);
        resultField.push(appCard);
    });
    infoDiv.textContent = "";//clear previous info and show new one
    const info = `Number of retrieved results: ${resultField.length}`;
    infoDiv.innerText = info;
    cardsContainer.textContent = "";//clear previous results and show new one
    resultField.forEach((card) => {
        card.render(cardsContainer);
    });

};

/**
* click listener for List button
*/
listBtn.addEventListener("click", getList);

/**
* click listener for Create button
*/
createBtn.addEventListener("click", () => {
    modalDialog.display(null, true);
});

operations.fillStatusSelection(statusSelection);


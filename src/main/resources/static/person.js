"use strict";

import { pathToServer, Person, PersonCard, AbstractOperations, MD} from './global.js';

class PersonOperations extends AbstractOperations{
    
    constructor(pathToServer) {
        super(pathToServer);
    };

    async create(object){
        let jsonData;
        jsonData = `{ "name":"${object.getName()}", "email":"${object.getEmail()}", "phone":"${object.getPhone()}"}`;
        const queryMethod = 'POST';
        const responseData = await fetch(this._pathToServer, {
            method: queryMethod,
            headers: {
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            body: jsonData,
        });
        this.processResponse(responseData);
    }

    async update(object){ 
        let jsonData = `{ "id":"${object.getId()}", "name":"${object.getName()}", "email":"${object.getEmail()}", "phone":"${object.getPhone()}"}`;
        const queryMethod = 'PUT';
        const responseData = await fetch(this._pathToServer, {
            method: queryMethod,
            headers: {
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            body: jsonData,
        });
        this.processResponse(responseData);
    }

    async delete(object){
            const queryMethod = 'DELETE';
            const query = `${this._pathToServer}/${object.getId()}`;
            const responseData = await fetch(query, {
                method: queryMethod,
            });
            this.processResponse(responseData);
        }
}

/**
 * class for modal dialog
 */
class ModalDialog extends MD{
    _span;
    _dialog;
    _modalEditBtn;
    _modalDeleteBtn;
    _modalCreateBtn;
    _modalId;
    _modalName;
    _modalEmail;
    _modalPhone;
    _PersOperations;
    _pathToServer;
        
    constructor(span, modalDialog, modalEditBtn, modalDeleteBtn, modalCreateBtn, modalPersId, modalPersName, modalEmail, modalPhone, pathToServer) {
        super();
        this._span = span;
        this._dialog = modalDialog;
        this._modalEditBtn = modalEditBtn;
        this._modalDeleteBtn = modalDeleteBtn;
        this._modalCreateBtn = modalCreateBtn;
        this._modalId = modalPersId;
        this._modalName = modalPersName;
        this._modalEmail = modalEmail;
        this._modalPhone = modalPhone;
        this._pathToServer = pathToServer;
        this._operations = new PersonOperations(this._pathToServer);
        this.addListeners();
    }
        
    addListeners() {
        this._span.addEventListener("click", () => {
            this._dialog.style.display = "none";
        });

        this._modalEditBtn.addEventListener("click", () => {
            this._operations.update(new Person(this.getId(), this.getName(), this.getEmail(), this.getPhone()));
            this.hide();
        });
            
        this._modalDeleteBtn.addEventListener("click", () => {
            this._operations.delete(new Person(this.getId(), this.getName(), this.getEmail(), this.getPhone()));
            this.hide();
        });

        this._modalCreateBtn.addEventListener('click', () => {
            this._operations.create(new Person('', this.getName(), this.getEmail(), this.getPhone()));
            this.hide();
        });        
    }
        
    hide() {
        this._dialog.style.display = "none";
    }
        
    /**
     * displays modal dialog
     * @param {} object - object to be shown in the dialog (in this case a Person)
     * @param {*} createNew - boolean. true->we create new one, false->we delete/modify existing one
     */
    display(object, createNew) {
        let person = object;
        if (createNew === true) {
            this._modalDeleteBtn.style.display = "none";
            this._modalEditBtn.style.display = 'none';
            this._modalCreateBtn.style.display = 'block';
        } else {
            this._modalDeleteBtn.style.display = "block";
            this._modalEditBtn.style.display = 'block';
            this._modalCreateBtn.style.display = 'none';
        }
        this._dialog.style.display = "block";
        if (person !== null) {
            this._modalId.value = person.getId().toString();
            this._modalName.value = person.getName();
            this._modalEmail.value = person.getEmail();
            this._modalPhone.value = person.getPhone();
        } else {
            this._modalId.value = '';
            this._modalName.value = '';
            this._modalEmail.value = '';
            this._modalPhone.value = '';
        }
    }
        
    getName() {
        return this._modalName.value;
    }
        
    getId() {
        return this._modalId.value;
    }

    getEmail(){
        return this._modalEmail.value;
    }

    getPhone(){
        return this._modalPhone.value;
    }
}

/************************************************************************/
/**
* global variables/constants
*/
//array with results
let resultField = [];
//path to server
//const pathToServer = "http://localhost:8080/JAM/person";
const idInput = document.getElementById("idInput");
const nameInput = document.getElementById("nameInput");
const emailInput = document.getElementById("emailInput");
const phoneInput = document.getElementById("phoneInput");
const resultDiv = document.getElementById("results");
const infoDiv = document.getElementById("info");
const cardsContainer = document.getElementById("cardsContainer");
//read buttons
const listBtn = document.querySelector("#listBtn");
const createBtn = document.querySelector("#createBtn");

//modal dialog
const modalDialog = new ModalDialog(document.getElementById("close"), 
                                    document.getElementById("modalDialog"),
                                    document.getElementById("modalEditBtn"),
                                    document.getElementById("modalDeleteBtn"),
                                    document.getElementById("modalCreateBtn"),
                                    document.getElementById("modalIdInput"), 
                                    document.getElementById("modalNameInput"),
                                    document.getElementById('modalEmailInput'),
                                    document.getElementById('modalPhoneInput'),
                                    `${pathToServer}person`)

/**
* gets list of persons according to filters
*/
const getList = async () => {
    let idValue = idInput.value;
    const nameValue = nameInput.value;
    const emailValue = emailInput.value;
    const phoneValue = phoneInput.value;
    if (idValue.length === 0 || parseInt(idValue) < 0) {
        idValue = '0';
    }
    let query = `${pathToServer}person?id=${idValue}&name=${nameValue}&email=${emailValue}&phone=${phoneValue}`;
    const data = await fetch(query).then((response) => response.json());
    
    //processResults(data);
    resultField = []; //clear array of previous results
    data.forEach((portion) => {//create card for each person obtained
        let personCard = new PersonCard(portion.id, portion.name, portion.email, portion.phone, portion.card, modalDialog);
        resultField.push(personCard);
    });
    infoDiv.textContent = '';//clear previous info and show new one
    const info = `Number of retrieved results: ${resultField.length}`;
    infoDiv.innerText = info;
    cardsContainer.textContent = '';//clear previous results and show new one
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

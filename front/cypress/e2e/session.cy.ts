import * as _ from 'lodash';
import {Session} from "../../src/app/features/sessions/interfaces/session.interface";
import {Teacher} from "../../src/app/interfaces/teacher.interface";

const login = (admin: boolean) => {
  cy.visit('/login')
  cy.intercept('POST', '/api/auth/login', {
    statusCode: 200,
    body: {
      id: 1,
      username: 'userName',
      firstName: 'firstName',
      lastName: 'lastName',
      admin: admin
    },
  })

  cy.get('input[formControlName=email]').type("yoga@studio.com")
  cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)
}

const yogaSession = (users: number[]): Session => {
  return {
    id: 1,
    teacher_id: _.random(2),
    createdAt: new Date("2024-10-29T17:51:57"),
    date: new Date("2024-10-29T16:52:03.000+00:00"),
    description: `this is a long description`,
    name: `Session`,
    updatedAt: new Date("2024-10-29T17:52:23"),
    users: users,
  }
}

const teacher = (id: number): Teacher => {
  return {
    id: id,
    firstName: "firstname" + id,
    lastName: "lastname" + id,
    createdAt: new Date(),
    updatedAt: new Date(),
  }
}

const yogaSessions = (number: number) => {
  const sessions = [];

  for (let i = 0; i < number; i++) {
    sessions.push({
      id: i,
      teacher_id: _.random(2),
      createdAt: "2024-10-29T17:51:57",
      date: "2024-10-29T16:52:03.000+00:00",
      description: `description ${i}`,
      name: `Session number ${i + 1}`,
      updatedAt: "2024-10-29T17:52:23",
      users: [],
    })
  }

  return sessions;
}

describe("Session list", () => {
  beforeEach(() => {
    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: yogaSessions(10),
    }).as('sessionList')
  })

  it('should show the session list as admin', () => {
    login(true)

    cy.wait('@sessionList');

    cy.get('div.items').should('be.visible');
    cy.get('mat-card.item').should('have.length', 10);
    cy.get('mat-card.item button .ml1').first().should('contain.text', 'Detail')
    cy.get('mat-card.item button .ml1').eq(1).should('contain.text', 'Edit')
    cy.get('mat-card button span.ml1').first().should('contain.text', 'Create')

  });

  it('should show the session list as user', () => {
    login(false)

    cy.wait('@sessionList');

    cy.get('div.items').should('be.visible');
    cy.get('mat-card.item').should('have.length', 10);
    cy.get('mat-card.item button .ml1').first().should('contain.text', 'Detail')
    cy.get('mat-card.item button .ml1').eq(1).should('not.contain.text', 'Edit')
    cy.get('mat-card button span.ml1').first().should('not.contain.text', 'Create')
  });
})

describe('Session Details', () => {
  beforeEach(() => {
    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: yogaSessions(10),
    }).as('sessionList')
  });

  it('should show the details of one session as not participating user and participate', () => {
    cy.intercept('GET', '/api/session/0', {
      statusCode: 200,
      body: yogaSession([2,3,4]),
    }).as('sessionDetails')
    cy.intercept('POST', '/api/session/0/participate/1', {
      statusCode: 200,
    }).as('sessionParticipate');
    login(false)
    cy.wait('@sessionList');

    cy.get('mat-card.item button .ml1').first().click()
    cy.wait('@sessionDetails');

    cy.get('.ml1').eq(0).should('contain.text', 'Participate').click();
    cy.wait('@sessionParticipate');
    cy.get('mat-card').first().should('contain.text', 'Description');
    cy.get('mat-card').first().should('contain.text', 'Session');
    cy.get('mat-card').first().should('contain.text', 'attendees');
    cy.get('mat-card').first().should('contain.text', 'Create');
    cy.get('mat-card').first().should('contain.html', 'img');
  });

  it('should show the details of one session as participating user and do not participate', () => {
    cy.intercept('GET', '/api/session/0', {
      statusCode: 200,
      body: yogaSession([1,2,3]),
    }).as('sessionDetails')
    cy.intercept('DELETE', '/api/session/0/participate/1', {
      statusCode: 200,
    }).as('sessionDoNotParticipate');
    login(false)
    cy.wait('@sessionList');

    cy.get('mat-card.item button .ml1').first().click()

    cy.wait('@sessionDetails');
    cy.get('.ml1').eq(0).should('contain.text', 'Do not participate').click();

    cy.get("div.description").first().should('contain.text', 'this is a long description');
    cy.get("h1").first().should('contain.text', 'Session');
    cy.wait('@sessionDoNotParticipate');
  });

  it('should show the details of one session as admin and delete', () => {
    cy.intercept('GET', '/api/session/0', {
      statusCode: 200,
      body: yogaSession([1,2,3]),
    }).as('sessionDetails')

    cy.intercept('DELETE', '/api/session/0', {
      statusCode: 200,
    }).as("sessionDelete");

    login(true)
    cy.wait('@sessionList');

    cy.get('mat-card.item button .ml1').first().click()

    cy.wait('@sessionDetails');
    cy.get('.ml1').eq(0).should('contain.text', 'Delete').click();
    cy.wait("@sessionDelete");
  });
})

describe("Session Creation", () => {
  beforeEach(() => {
    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: yogaSessions(10),
    }).as('sessionList')

    cy.intercept('GET', '/api/teacher', {
      statusCode: 200,
      body: [teacher(1), teacher(2)],
    }).as('sessionList')
  });

  it('should create a session', () => {
    login(true)

    cy.intercept('POST', '/api/session', {
      statusCode: 200,
    }).as('sessionCreate')

    cy.wait('@sessionList');
    cy.get('mat-card button span.ml1').first().should('contain.text', 'Create').click()

    cy.get('form button').should('be.disabled')
    cy.get('form [formControlName="name"]').should('contain.text', '').type(`${"Name"}`)
    cy.get('form [formControlName="date"]').should('contain.text', '').type('2023-10-12')
    cy.get('form [formControlName="teacher_id"]').should('contain.text', '').type('{enter}{downArrow}')
    cy.get('form [formControlName="description"]').should('contain.text', '').type('My super very long description')
    cy.get('form button').should('contain.text', 'Save').click()
    cy.wait('@sessionCreate');
    cy.url().should('include', '/sessions');
  });
})

describe("should edit a session", () => {
  beforeEach(() => {
    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: yogaSessions(20),
    }).as('sessionList')
  })

  it('should show the session list as admin', () => {
    login(true)
    cy.intercept('GET', '/api/session/0', {
      statusCode: 200,
      body: yogaSession([1,2,3]),
    }).as('sessionDetails')

    cy.intercept('PUT', '/api/session/0', {
      statusCode: 200,
    }).as('sessionUpdate')

    cy.wait('@sessionList');
    cy.get('mat-card.item button .ml1').eq(1).should('contain.text', 'Edit').click();
    cy.url().should('include', '/update/0');
    cy.wait('@sessionDetails');
    cy.get('form [formControlName="name"]').type(`${"Name"}`)
    cy.get('form [formControlName="date"]').type('2023-10-12')
    cy.get('form [formControlName="teacher_id"]').type('{enter}{downArrow}')
    cy.get('form [formControlName="description"]').type('My super very long description')
    cy.get('form button').should('contain.text', 'Save').click()
    cy.wait("@sessionUpdate");
    cy.url().should('include', '/sessions');
  })
});


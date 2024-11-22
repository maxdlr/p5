import { cyUtils } from './utils-cy';
import { User } from '../../src/app/interfaces/user.interface';

const user: User = cyUtils.user(1);
const admin: User = cyUtils.user(2, true);

describe('Session list', () => {
  beforeEach(() => {
    cyUtils.expectSessionList();
  });

  it('should show the session list as admin', () => {
    cyUtils.login(admin);
    cy.wait('@sessionList');

    cy.get('div.items').should('be.visible');
    cy.get('mat-card.item').should('have.length', 10);
    cy.get('mat-card.item button .ml1')
      .first()
      .should('contain.text', 'Detail');
    cy.get('mat-card.item button .ml1').eq(1).should('contain.text', 'Edit');
    cy.get('mat-card button span.ml1').first().should('contain.text', 'Create');
  });

  it('should show the session list as user', () => {
    cyUtils.login(user);
    cy.wait('@sessionList');

    cy.get('div.items').should('be.visible');
    cy.get('mat-card.item').should('have.length', 10);
    cy.get('mat-card.item button .ml1')
      .first()
      .should('contain.text', 'Detail');
    cy.get('mat-card.item button .ml1')
      .eq(1)
      .should('not.contain.text', 'Edit');
    cy.get('mat-card button span.ml1')
      .first()
      .should('not.contain.text', 'Create');
  });
});

describe('Session Details', () => {
  beforeEach(() => {
    cyUtils.expectSessionList();
  });

  it('should show the details of one session as not participating user and participate', () => {
    cy.intercept('GET', '/api/session/0', {
      statusCode: 200,
      body: cyUtils.yogaSession([2, 3, 4]),
    }).as('sessionDetails');
    cy.intercept('POST', '/api/session/0/participate/1', {
      statusCode: 200,
    }).as('sessionParticipate');
    cyUtils.login(user);
    cy.wait('@sessionList');

    cy.get('mat-card.item button .ml1').first().click();
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
      body: cyUtils.yogaSession([1, 2, 3]),
    }).as('sessionDetails');
    cy.intercept('DELETE', '/api/session/0/participate/1', {
      statusCode: 200,
    }).as('sessionDoNotParticipate');
    cyUtils.login(user);
    cy.wait('@sessionList');

    cy.get('mat-card.item button .ml1').first().click();

    cy.wait('@sessionDetails');
    cy.get('.ml1').eq(0).should('contain.text', 'Do not participate').click();

    cy.get('div.description')
      .first()
      .should('contain.text', 'this is a long description');
    cy.get('h1').first().should('contain.text', 'Session');
    cy.wait('@sessionDoNotParticipate');
  });

  it('should show the details of one session as admin and delete', () => {
    cy.intercept('GET', '/api/session/0', {
      statusCode: 200,
      body: cyUtils.yogaSession([1, 2, 3]),
    }).as('sessionDetails');

    cy.intercept('DELETE', '/api/session/0', {
      statusCode: 200,
    }).as('sessionDelete');

    cyUtils.login(admin);
    cy.wait('@sessionList');

    cy.get('mat-card.item button .ml1').first().click();

    cy.wait('@sessionDetails');
    cy.get('.ml1').eq(0).should('contain.text', 'Delete').click();
    cy.wait('@sessionDelete');
  });
});

describe('Session Creation', () => {
  beforeEach(() => {
    cyUtils.expectSessionList();

    cy.intercept('GET', '/api/teacher', {
      statusCode: 200,
      body: [cyUtils.teacher(1), cyUtils.teacher(2)],
    }).as('sessionList');
  });

  it('should create a session', () => {
    cyUtils.login(admin);

    cy.intercept('POST', '/api/session', {
      statusCode: 200,
    }).as('sessionCreate');

    cy.wait('@sessionList');
    cy.get('mat-card button span.ml1')
      .first()
      .should('contain.text', 'Create')
      .click();

    cy.get('form button').should('be.disabled');
    cy.get('form [formControlName="name"]')
      .should('contain.text', '')
      .type(`Name`);
    cy.get('form [formControlName="date"]')
      .should('contain.text', '')
      .type('2023-10-12');
    cy.get('form [formControlName="teacher_id"]')
      .should('contain.text', '')
      .type('{enter}{downArrow}');
    cy.get('form [formControlName="description"]')
      .should('contain.text', '')
      .type('My super very long description');
    cy.get('form button').should('contain.text', 'Save').click();
    cy.wait('@sessionCreate');
    cy.url().should('include', '/sessions');
  });
});

describe('should edit a session', () => {
  beforeEach(() => {
    cyUtils.expectSessionList();
  });

  it('should show the session list as admin', () => {
    cyUtils.login(admin);
    cy.intercept('GET', '/api/session/0', {
      statusCode: 200,
      body: cyUtils.yogaSession([1, 2, 3]),
    }).as('sessionDetails');

    cy.intercept('PUT', '/api/session/0', {
      statusCode: 200,
    }).as('sessionUpdate');

    cy.wait('@sessionList');
    cy.get('mat-card.item button .ml1')
      .eq(1)
      .should('contain.text', 'Edit')
      .click();
    cy.url().should('include', '/update/0');
    cy.wait('@sessionDetails');
    cy.get('form [formControlName="name"]').type('Name');
    cy.get('form [formControlName="date"]').type('2023-10-12');
    cy.get('form [formControlName="teacher_id"]').type('{enter}{downArrow}');
    cy.get('form [formControlName="description"]').type(
      'My super very long description',
    );
    cy.get('form button').should('contain.text', 'Save').click();
    cy.wait('@sessionUpdate');
    cy.url().should('include', '/sessions');
  });
});

describe('Login spec', () => {
  beforeEach(() => {
    cy.visit('/login');

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      [],
    ).as('session');
  });

  it('Login successfull', () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true,
      },
    }).as('loginSuccess');

    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type(
      `${'test!1234'}{enter}{enter}`,
    );

    cy.wait('@loginSuccess');
    cy.url().should('include', '/sessions');
  });

  it('Login failure', () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401, // HTTP status code for unauthorized
      body: {
        message: 'Invalid credentials', // You can customize the error message
      },
    }).as('loginFailure');
    cy.get('input[formControlName=email]').type('invalidUser@studio.com');
    cy.get('input[formControlName=password]').type(
      `${'wrongPassword'}{enter}{enter}`,
    );

    cy.wait('@loginFailure');

    cy.contains('An error occurred').should('be.visible'); // Adjust this selector/message to match your app
    cy.url().should('not.include', '/sessions');
  });
});

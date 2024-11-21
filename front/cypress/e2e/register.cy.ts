const fillForm = () => {
  cy.get('input[formControlName=firstName]')
    .should('be.visible')
    .type('Maxime');
  cy.get('input[formControlName=lastName]').should('be.visible').type('dlr');
  cy.get('input[formControlName=email]')
    .should('be.visible')
    .type('email@email.com');
  cy.get('input[formControlName=password]')
    .should('be.visible')
    .type(`${'password'}{enter}{enter}`);
};

describe('Register spec', () => {
  beforeEach(() => {
    cy.visit('/register');
  });

  it('registers the new user successfully', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 200,
      body: { message: 'User registered successfully!' },
    }).as('registerSuccess');

    fillForm();

    cy.wait('@registerSuccess');

    cy.url().should('include', '/login');
  });

  it('should show the error', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 401,
      body: { message: 'User email already registered.' },
    }).as('registerFailure');

    fillForm();

    cy.wait('@registerFailure');

    cy.contains('An error occurred').should('be.visible');
    cy.url().should('not.include', '/login');
  });
});

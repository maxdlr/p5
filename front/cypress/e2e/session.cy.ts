const login = () => {
  cy.visit('/login')
  cy.intercept('POST', '/api/auth/login', {
    statusCode: 200,
    body: {
      id: 1,
      username: 'userName',
      firstName: 'firstName',
      lastName: 'lastName',
      admin: true
    },
  })

  cy.get('input[formControlName=email]').type("yoga@studio.com")
  cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)
}

describe("Session list", () => {
  beforeEach(() => {
    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: [
        {
          createdAt: "2024-10-29T17:51:57",
          date: "2024-10-29T16:52:03.000+00:00",
          description: "long description",
          id: 1,
          name: "this sessions name",
          teacher_id: 1,
          updatedAt: "2024-10-29T17:52:23",
          users: [],
        }
      ],
    }).as('sessionList')
    login()
  })

  it('should show the session list', () => {
    cy.wait('@sessionList');
  });
})

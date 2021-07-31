module.exports = {
    env: {
        node: true,
    },
    parser: '@typescript-eslint/parser',
    extends: ['plugin:@typescript-eslint/recommended', 'plugin:prettier/recommended'],
    parserOptions: {
        ecmaVersion: 11,
        sourceType: 'module',
    },
    plugins: ['@typescript-eslint'],
    rules: {
        'prettier/prettier': 1,
    },
    overrides: [
        {
            files: ['**/*.js'],
            rules: {
                '@typescript-eslint/no-var-requires': 'off',
            },
        },
    ],
}

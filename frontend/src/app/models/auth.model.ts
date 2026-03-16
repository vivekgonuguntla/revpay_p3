export interface SecurityQuestionDto {
    question: string;
    answer: string;
}

export interface RegisterRequest {
    fullName: string;
    username: string;
    email: string;
    password: string;
    phoneNumber: string;
    role: 'PERSONAL' | 'BUSINESS';
    securityQuestions: SecurityQuestionDto[];
    // Business specific
    businessName?: string;
    businessType?: string;
    taxId?: string;
    businessAddress?: string;
    verificationDocsPath?: string;
}

export interface AuthRequest {
    email: string;
    password: string;
}

export interface AuthResponse {
    token: string;
    userId: number;
    username: string;
    email: string;
    role: string;
}

export interface User {
    userId?: number;
    username: string;
    email: string;
    role: string;
    token?: string;
}

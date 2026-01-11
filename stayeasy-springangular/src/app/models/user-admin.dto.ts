export interface UserADMIN_DTO {
  id: number;
  username: string;
  email: string;
  fullName: string;
  createdAt: string; // LocalDateTime → ISO string
  role: 'ADMIN' | 'HOST' | 'GUEST';
}

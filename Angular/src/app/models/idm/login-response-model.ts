import { Deserializable } from 'src/app/interfaces/deserializable';

export class LoginResponseModel implements Deserializable{
    resultCode: number;
    message: string;
    sessionID: string;

    deserialize(input: any) {
        Object.assign(this, input);
        return this;
    }
}
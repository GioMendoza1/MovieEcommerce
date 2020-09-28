import { Deserializable } from 'src/app/interfaces/deserializable';

export class RegisterResponseModel implements Deserializable{
    resultCode: number;
    message: string;

    deserialize(input: any) {
        Object.assign(this, input);
        return this;
    }
}
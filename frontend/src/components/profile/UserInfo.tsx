import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import * as z from 'zod'
import { Edit2, User, Mail, Calendar, Award, Heart, BookOpen, Loader2 } from 'lucide-react'
import { UserProfile, UpdateProfileRequest } from '@/types/user'
import { useUpdateProfile } from '@/hooks/useUser'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { Label } from '@/components/ui/label'
import { Badge } from '@/components/ui/badge'
import { formatDistanceToNow } from 'date-fns'

const profileSchema = z.object({
  firstName: z.string().min(2, 'First name must be at least 2 characters'),
  lastName: z.string().min(2, 'Last name must be at least 2 characters'),
  bio: z.string().max(500, 'Bio must be less than 500 characters').optional(),
})

type ProfileFormData = z.infer<typeof profileSchema>

interface UserInfoProps {
  user: UserProfile
  className?: string
}

export default function UserInfo({ user, className }: UserInfoProps) {
  const [isEditing, setIsEditing] = useState(false)
  const updateProfileMutation = useUpdateProfile()

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    reset,
  } = useForm<ProfileFormData>({
    resolver: zodResolver(profileSchema),
    defaultValues: {
      firstName: user.firstName,
      lastName: user.lastName,
      bio: user.bio || '',
    },
  })

  const handleEdit = () => {
    setIsEditing(true)
    reset({
      firstName: user.firstName,
      lastName: user.lastName,
      bio: user.bio || '',
    })
  }

  const handleCancel = () => {
    setIsEditing(false)
    reset()
  }

  const onSubmit = async (data: ProfileFormData) => {
    try {
      await updateProfileMutation.mutateAsync(data)
      setIsEditing(false)
    } catch (error) {
      // Error handling is done in the mutation hook
    }
  }

  const memberSince = formatDistanceToNow(new Date(user.createdAt), { addSuffix: true })

  return (
    <Card className={className}>
      <CardHeader>
        <div className="flex items-center justify-between">
          <CardTitle className="flex items-center gap-2">
            <User className="h-5 w-5" />
            Profile Information
          </CardTitle>
          {!isEditing && (
            <Button variant="outline" size="sm" onClick={handleEdit}>
              <Edit2 className="h-4 w-4 mr-2" />
              Edit
            </Button>
          )}
        </div>
      </CardHeader>
      <CardContent>
        {isEditing ? (
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="firstName">First Name</Label>
                <Input
                  id="firstName"
                  {...register('firstName')}
                  className={errors.firstName ? 'border-red-500' : ''}
                />
                {errors.firstName && (
                  <p className="text-sm text-red-500">{errors.firstName.message}</p>
                )}
              </div>
              <div className="space-y-2">
                <Label htmlFor="lastName">Last Name</Label>
                <Input
                  id="lastName"
                  {...register('lastName')}
                  className={errors.lastName ? 'border-red-500' : ''}
                />
                {errors.lastName && (
                  <p className="text-sm text-red-500">{errors.lastName.message}</p>
                )}
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="bio">Bio</Label>
              <Textarea
                id="bio"
                placeholder="Tell us about yourself..."
                rows={3}
                {...register('bio')}
                className={errors.bio ? 'border-red-500' : ''}
              />
              {errors.bio && (
                <p className="text-sm text-red-500">{errors.bio.message}</p>
              )}
            </div>

            <div className="flex gap-3 pt-4">
              <Button 
                type="submit" 
                disabled={isSubmitting || updateProfileMutation.isPending}
              >
                {(isSubmitting || updateProfileMutation.isPending) ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    Saving...
                  </>
                ) : (
                  'Save Changes'
                )}
              </Button>
              <Button 
                type="button" 
                variant="outline" 
                onClick={handleCancel}
                disabled={isSubmitting || updateProfileMutation.isPending}
              >
                Cancel
              </Button>
            </div>
          </form>
        ) : (
          <div className="space-y-6">
            {/* User Avatar */}
            <div className="flex items-center gap-4">
              <div className="w-16 h-16 bg-gradient-to-br from-blue-500 to-purple-600 rounded-full flex items-center justify-center text-white text-xl font-bold">
                {user.firstName[0]}{user.lastName[0]}
              </div>
              <div>
                <h3 className="text-xl font-semibold">
                  {user.firstName} {user.lastName}
                </h3>
                <div className="flex items-center gap-2 text-sm text-muted-foreground">
                  <Mail className="h-4 w-4" />
                  {user.email}
                </div>
                <div className="flex items-center gap-2 text-sm text-muted-foreground">
                  <Calendar className="h-4 w-4" />
                  Member {memberSince}
                </div>
              </div>
            </div>

            {/* Bio */}
            {user.bio && (
              <div>
                <h4 className="font-medium mb-2">About</h4>
                <p className="text-gray-700 leading-relaxed">{user.bio}</p>
              </div>
            )}

            {/* User Stats */}
            <div className="grid grid-cols-3 gap-4 pt-4 border-t">
              <div className="text-center">
                <div className="flex items-center justify-center gap-1 text-2xl font-bold text-blue-600">
                  <BookOpen className="h-5 w-5" />
                  {user.totalReviews || 0}
                </div>
                <p className="text-sm text-muted-foreground">Reviews</p>
              </div>
              <div className="text-center">
                <div className="flex items-center justify-center gap-1 text-2xl font-bold text-red-600">
                  <Heart className="h-5 w-5" />
                  {user.totalFavoriteBooks || 0}
                </div>
                <p className="text-sm text-muted-foreground">Favorites</p>
              </div>
              <div className="text-center">
                <div className="flex items-center justify-center gap-1 text-2xl font-bold text-yellow-600">
                  <Award className="h-5 w-5" />
                  {user.averageRating ? user.averageRating.toFixed(1) : 'N/A'}
                </div>
                <p className="text-sm text-muted-foreground">Avg Rating</p>
              </div>
            </div>

            {/* Role Badge */}
            <div className="flex items-center gap-2">
              <span className="text-sm font-medium">Role:</span>
              <Badge variant={user.role === 'ADMIN' ? 'default' : 'secondary'}>
                {user.role}
              </Badge>
            </div>
          </div>
        )}
      </CardContent>
    </Card>
  )
}
